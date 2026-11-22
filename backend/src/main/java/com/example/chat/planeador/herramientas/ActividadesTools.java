package com.example.chat.planeador.herramientas;

import com.example.chat.planeador.Progreso;
import com.example.chat.planeador.datos.DatosPlaneador;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Tools del AGENTE DE ACTIVIDADES.
 */
@Component
public class ActividadesTools {

    private final DatosPlaneador datos;
    private final Progreso progreso;

    /** Constructor usado por Spring. */
    @Autowired
    public ActividadesTools(DatosPlaneador datos, Progreso progreso) {
        this.datos = datos;
        this.progreso = progreso;
    }

    /** Para pruebas JUnit sin Spring: sin panel de actividad. */
    public ActividadesTools(DatosPlaneador datos) {
        this(datos, new Progreso());
    }

    @Tool(description = "Recomienda planes y actividades turísticas disponibles en un destino de fin de semana.")
    public String recomendarActividades(
            @ToolParam(description = "Nombre del destino, por ejemplo Villa de Leyva") String destino) {
        return progreso.registrar("actividades", "recomendarActividades", Map.of("destino", String.valueOf(destino)),
                () -> recomendarActividadesImpl(destino));
    }

    private String recomendarActividadesImpl(String destino) {
        String clave = datos.buscarDestino(destino);
        if (clave == null) {
            return "No conozco el destino '%s'. Destinos válidos: %s.".formatted(destino, datos.destinosValidos());
        }
        List<String> planes = datos.actividades(clave);
        return "Actividades en %s: %s.".formatted(datos.destino(clave).nombre(), String.join("; ", planes));
    }
}
