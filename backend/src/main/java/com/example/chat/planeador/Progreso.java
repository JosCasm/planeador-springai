package com.example.chat.planeador;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Canal por el que los agentes y las tools informan lo que van haciendo (panel de actividad del frontend).
 *
 * Spring AI ejecuta las tools en el mismo hilo de la petición, así que un ThreadLocal da a cada
 * petición su propio oyente. Si no hay oyente (chat normal, pruebas JUnit) no hace nada.
 */
@Component
public class Progreso {

    private final ThreadLocal<Consumer<Map<String, Object>>> oyente = new ThreadLocal<>();

    public void iniciar(Consumer<Map<String, Object>> consumidor) { oyente.set(consumidor); }

    public void terminar() { oyente.remove(); }

    public void emitir(Map<String, Object> evento) {
        Consumer<Map<String, Object>> c = oyente.get();
        if (c != null) c.accept(evento);
    }

    /** Construye un evento a partir de pares clave, valor. */
    public static Map<String, Object> evento(Object... pares) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < pares.length; i += 2) m.put((String) pares[i], pares[i + 1]);
        return m;
    }

    /** Ejecuta una tool reportando la llamada (con sus argumentos) y su resultado. */
    public String registrar(String agente, String tool, Map<String, Object> args, Supplier<String> accion) {
        emitir(evento("tipo", "tool_call", "agente", agente, "tool", tool, "args", args));
        String resultado = accion.get();
        emitir(evento("tipo", "tool_result", "agente", agente, "texto", resultado));
        return resultado;
    }
}
