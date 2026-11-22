package com.example.chat.planeador;

import com.example.chat.dto.ChatRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * Variante de /api/v1/chat que emite el progreso de los agentes (NDJSON: una línea JSON por evento).
 * El último evento es de tipo "final" con la respuesta. El ChatController original no se modifica.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1")
public class ChatStreamController {

    private static final Logger log = LoggerFactory.getLogger(ChatStreamController.class);
    private final PlaneadorOrquestador orquestador;

    public ChatStreamController(PlaneadorOrquestador orquestador) {
        this.orquestador = orquestador;
    }

    @PostMapping(value = "/chat/stream", produces = "application/x-ndjson")
    public ResponseEntity<StreamingResponseBody> stream(@Valid @RequestBody ChatRequest req) {
        StreamingResponseBody cuerpo = salida -> {
            long inicio = System.currentTimeMillis();
            String rid = UUID.randomUUID().toString();
            try {
                String answer = orquestador.planear(req.getQuestion(), ev -> escribir(salida, ev));
                boolean ok = answer != null && !answer.isBlank();
                escribir(salida, Progreso.evento("tipo", "final", "answer", answer == null ? "" : answer,
                        "requestId", rid, "elapsedMs", System.currentTimeMillis() - inicio,
                        "status", ok ? "ok" : "error"));
            } catch (Exception e) {
                log.error("Error en el stream del planeador", e);
                escribir(salida, Progreso.evento("tipo", "error", "texto", String.valueOf(e.getMessage())));
            }
        };
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-ndjson")).body(cuerpo);
    }

    private static void escribir(OutputStream salida, Map<String, Object> evento) {
        try {
            salida.write((Json.toJson(evento) + "\n").getBytes(StandardCharsets.UTF_8));
            salida.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
