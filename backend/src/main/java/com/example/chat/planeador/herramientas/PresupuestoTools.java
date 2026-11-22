package com.example.chat.planeador.herramientas;

import com.example.chat.planeador.datos.DatosPlaneador;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import com.example.chat.planeador.Progreso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.example.chat.planeador.datos.DatosPlaneador.pesos;

/** Tools del AGENTE DE PRESUPUESTO: las cuentas las hace Java, no el modelo. */
@Component
public class PresupuestoTools {

    private final DatosPlaneador datos;
    private final Progreso progreso;

    /** Constructor usado por Spring. */
    @Autowired
    public PresupuestoTools(DatosPlaneador datos, Progreso progreso) {
        this.datos = datos;
        this.progreso = progreso;
    }

    /** Para pruebas JUnit sin Spring: sin panel de actividad. */
    public PresupuestoTools(DatosPlaneador datos) {
        this(datos, new Progreso());
    }

    @Tool(description = "Calcula el costo total de ir a un destino (transporte, entrada y alimentación) "
            + "para un número de personas.")
    public String estimarCosto(
            @ToolParam(description = "Nombre del destino") String destino,
            @ToolParam(description = "Número de personas del grupo") int personas) {
        return progreso.registrar("presupuesto", "estimarCosto", Map.of("destino", String.valueOf(destino), "personas", String.valueOf(personas)),
                () -> estimarCostoImpl(destino, personas));
    }

    private String estimarCostoImpl(String destino, int personas) {
        String clave = datos.buscarDestino(destino);
        if (clave == null) {
            return "No conozco el destino '%s'. Destinos válidos: %s.".formatted(destino, datos.destinosValidos());
        }
        if (personas <= 0) {
            return "El número de personas debe ser mayor que cero.";
        }
        DatosPlaneador.Destino c = datos.destino(clave);
        int total = c.costoPorPersona() * personas;
        return "%s: por persona transporte %s + entrada %s + alimentación %s = %s. Costo total para %d personas: %s."
                .formatted(c.nombre(), pesos(c.transporte()), pesos(c.entrada()), pesos(c.alimentacion()),
                        pesos(c.costoPorPersona()), personas, pesos(total));
    }

    @Tool(description = "Compara el presupuesto disponible del grupo con el costo total del plan "
            + "y dice si alcanza el dinero.")
    public String calcularSaldo(
            @ToolParam(description = "Presupuesto total del grupo en pesos, sin puntos ni signos") int presupuesto,
            @ToolParam(description = "Costo total del plan en pesos, sin puntos ni signos") int costoTotal) {
        return progreso.registrar("presupuesto", "calcularSaldo", Map.of("presupuesto", String.valueOf(presupuesto), "costoTotal", String.valueOf(costoTotal)),
                () -> calcularSaldoImpl(presupuesto, costoTotal));
    }

    private String calcularSaldoImpl(int presupuesto, int costoTotal) {
        int saldo = presupuesto - costoTotal;
        if (saldo >= 0) {
            return "Sí alcanza: presupuesto %s - costo %s = sobran %s."
                    .formatted(pesos(presupuesto), pesos(costoTotal), pesos(saldo));
        }
        return "No alcanza: presupuesto %s - costo %s = faltan %s."
                .formatted(pesos(presupuesto), pesos(costoTotal), pesos(-saldo));
    }
}
