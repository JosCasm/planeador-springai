package com.example.chat.planeador;

import com.example.chat.planeador.Decision.Agente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Orquestador multiagente (patrón SUPERVISOR / orchestrator-workers).
 *
 * Es el equivalente en Java del grafo de LangGraph:
 *
 *    pregunta -> [supervisor] --(decide)--> clima | presupuesto | reservas --> vuelve al supervisor
 *                     |
 *                    FIN --> [sintetizador] -> respuesta
 *
 * En LangGraph el flujo se declara como nodos y aristas; en Spring AI lo escribimos
 * como código Java normal (un ciclo while). El "estado" son las variables locales
 * pregunta, resultados y consultados.
 */
@Service
public class PlaneadorOrquestador {

    private static final Logger log = LoggerFactory.getLogger(PlaneadorOrquestador.class);

    /** Límite de autonomía: máximo de especialistas por pregunta. */
    private static final int MAX_PASOS = 4;

    private final Progreso progreso;
    private final ChatClient supervisor;
    private final ChatClient sintetizador;
    private final Map<Agente, ChatClient> especialistas = new EnumMap<>(Agente.class);

    public PlaneadorOrquestador(Progreso progreso,
                                @Qualifier("supervisor") ChatClient supervisor,
                                @Qualifier("sintetizador") ChatClient sintetizador,
                                @Qualifier("agenteClima") ChatClient agenteClima,
                                @Qualifier("agentePresupuesto") ChatClient agentePresupuesto,
                                @Qualifier("agenteReservas") ChatClient agenteReservas,
                                @Qualifier("agenteActividades") ChatClient agenteActividades) {
        this.progreso = progreso;
        this.supervisor = supervisor;
        this.sintetizador = sintetizador;
        especialistas.put(Agente.CLIMA, agenteClima);
        especialistas.put(Agente.PRESUPUESTO, agentePresupuesto);
        especialistas.put(Agente.RESERVAS, agenteReservas);
        especialistas.put(Agente.ACTIVIDADES, agenteActividades);
    }

    public String planear(String pregunta) {
        return planear(pregunta, evento -> { });
    }

    /** Igual que planear(pregunta), pero informa cada paso (supervisor, tools, agentes) a {@code eventos}. */
    public String planear(String pregunta, Consumer<Map<String, Object>> eventos) {
        progreso.iniciar(eventos);
        try {
            return ejecutar(pregunta);
        } finally {
            progreso.terminar();
        }
    }

    private String ejecutar(String pregunta) {
        List<String> resultados = new ArrayList<>();
        Set<Agente> consultados = EnumSet.noneOf(Agente.class);

        while (consultados.size() < MAX_PASOS) {
            Agente siguiente = decidir(pregunta, resultados, consultados);
            if (siguiente == null || siguiente == Agente.FIN || consultados.contains(siguiente)) {
                break;                                   // evita ciclos infinitos
            }
            String nombre = siguiente.name().toLowerCase();
            String respuesta = especialistas.get(siguiente).prompt()
                    .user(contexto(pregunta, resultados))
                    .call()
                    .content();
            log.info("[{}] {}", siguiente, respuesta);
            resultados.add("[" + siguiente + "] " + respuesta);
            consultados.add(siguiente);
            progreso.emitir(Progreso.evento("tipo", "agente", "agente", nombre, "texto", String.valueOf(respuesta)));
        }

        progreso.emitir(Progreso.evento("tipo", "supervisor", "siguiente", "fin",
                "motivo", "información suficiente o límite de pasos alcanzado"));
        return sintetizador.prompt()
                .user(contexto(pregunta, resultados))
                .call()
                .content();
    }

    /** Nodo "supervisor": pide al modelo una Decision (salida estructurada). */
    private Agente decidir(String pregunta, List<String> resultados, Set<Agente> consultados) {
        String yaConsultados = consultados.isEmpty() ? "ninguno"
                : consultados.stream().map(Enum::name).collect(Collectors.joining(", "));
        try {
            Decision decision = supervisor.prompt()
                    .user(contexto(pregunta, resultados) + "\n\nAgentes ya consultados: " + yaConsultados)
                    .call()
                    .entity(Decision.class);
            log.info("[SUPERVISOR] -> {} ({})", decision.siguiente(), decision.motivo());
            if (decision.siguiente() != Agente.FIN && !consultados.contains(decision.siguiente())) {
                progreso.emitir(Progreso.evento("tipo", "supervisor",
                        "siguiente", decision.siguiente().name().toLowerCase(), "motivo", decision.motivo()));
            }
            return decision.siguiente();
        } catch (RuntimeException e) {                   // el modelo no devolvió un JSON válido
            log.warn("[SUPERVISOR] decisión inválida ({}); se pasa a la respuesta final", e.getMessage());
            return Agente.FIN;
        }
    }

    private static String contexto(String pregunta, List<String> resultados) {
        String previos = resultados.isEmpty() ? "(todavía no hay resultados)" : String.join("\n", resultados);
        return "Pregunta del usuario: " + pregunta + "\n\nResultados de otros agentes:\n" + previos;
    }
}
