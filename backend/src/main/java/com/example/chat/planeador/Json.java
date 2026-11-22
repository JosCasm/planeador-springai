package com.example.chat.planeador;

import java.util.Map;
import java.util.StringJoiner;

/** Serializador JSON mínimo para los eventos del stream (evita depender de la versión de Jackson). */
final class Json {

    private Json() {}

    static String toJson(Object valor) {
        if (valor == null) return "null";
        if (valor instanceof Number || valor instanceof Boolean) return valor.toString();
        if (valor instanceof Map<?, ?> mapa) {
            StringJoiner sj = new StringJoiner(",", "{", "}");
            mapa.forEach((k, v) -> sj.add(toJson(String.valueOf(k)) + ":" + toJson(v)));
            return sj.toString();
        }
        StringBuilder sb = new StringBuilder("\"");
        for (char c : valor.toString().toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> { if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c); }
            }
        }
        return sb.append('"').toString();
    }
}
