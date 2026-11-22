# Resultados de las 6 Preguntas Base — Spring AI + Ollama (`qwen2.5:3b`)

## Resumen de tiempos y estado

| ID | Pregunta | Status | `elapsedMs` (ms) | Tiempo (s) |
|---|---|---|---:|---:|
| **P1** | ¿Cómo estará el clima en Guatavita el domingo? | `ok` | 18840 | 18.84 |
| **P2** | Somos 4 amigos con 350000 pesos. ¿Nos alcanza para ir a Zipaquirá? | `ok` | 25601 | 25.60 |
| **P3** | Somos 2 y queremos ir a Villa de Leyva el sábado. ¿Cómo estará el clima y cuánto nos cuesta? | `ok` | 31646 | 31.65 |
| **P4** | Somos 3 y tenemos 250000. Si no llueve mucho y nos alcanza, reserva Suesca para el sábado a nombre de Laura. | `ok` | 34122 | 34.12 |
| **P5** | Somos 3 con 150000. Si nos alcanza, reserva Suesca el domingo a nombre de Laura. | `ok` | 24459 | 24.46 |
| **P6** | ¿Quién ganó el mundial de 2022? | `ok` | 11248 | 11.25 |
| **Promedio** | — | — | **24319.33** | **24.32** |
| **Total** | — | — | **145916** | **145.92** |

---

## Detalle de respuestas exactas

### P1: ¿Cómo estará el clima en Guatavita el domingo?

- **`requestId`**: `a120f2c1-b801-451e-a367-8a925a634581`
- **`status`**: `ok`
- **`elapsedMs`**: `18840` ms (18.84 s)

**Respuesta (`answer`):**

```text
El domingo en Guatavita será lluvioso con temperaturas entre 7 °C y 15 °C. Es recomendable llevar una chaqueta o impermeable.
```

**JSON crudo:**

```json
{
  "requestId": "a120f2c1-b801-451e-a367-8a925a634581",
  "answer": "El domingo en Guatavita será lluvioso con temperaturas entre 7 °C y 15 °C. Es recomendable llevar una chaqueta o impermeable.",
  "elapsedMs": 18840,
  "status": "ok"
}
```

### P2: Somos 4 amigos con 350000 pesos. ¿Nos alcanza para ir a Zipaquirá?

- **`requestId`**: `4e598538-c3bf-4393-b1f0-fcd86dd6e3ec`
- **`status`**: `ok`
- **`elapsedMs`**: `25601` ms (25.60 s)

**Respuesta (`answer`):**

```text
No alcanza el presupuesto. Faltan 500000 pesos para la reserva a Zipaquirá con 350000 pesos.
```

**JSON crudo:**

```json
{
  "requestId": "4e598538-c3bf-4393-b1f0-fcd86dd6e3ec",
  "answer": "No alcanza el presupuesto. Faltan 500000 pesos para la reserva a Zipaquirá con 350000 pesos.",
  "elapsedMs": 25601,
  "status": "ok"
}
```

### P3: Somos 2 y queremos ir a Villa de Leyva el sábado. ¿Cómo estará el clima y cuánto nos cuesta?

- **`requestId`**: `05f98fb4-affd-425d-9b42-54e207928299`
- **`status`**: `ok`
- **`elapsedMs`**: `31646` ms (31.65 s)

**Respuesta (`answer`):**

```text
El clima para Villa de Leyva el sábado será soleado con temperaturas entre 12 °C y 25 °C. No hay riesgo de lluvia. El costo total para ir a Villa de Leyva para 2 personas será de 2300000 pesos. Podemos reservar tu viaje a Villa de Leyva para el sábado. El costo será de 2300000 pesos. ¿Deseas reservar?
```

**JSON crudo:**

```json
{
  "requestId": "05f98fb4-affd-425d-9b42-54e207928299",
  "answer": "El clima para Villa de Leyva el sábado será soleado con temperaturas entre 12 °C y 25 °C. No hay riesgo de lluvia. El costo total para ir a Villa de Leyva para 2 personas será de 2300000 pesos. Podemos reservar tu viaje a Villa de Leyva para el sábado. El costo será de 2300000 pesos. ¿Deseas reservar?",
  "elapsedMs": 31646,
  "status": "ok"
}
```

### P4: Somos 3 y tenemos 250000. Si no llueve mucho y nos alcanza, reserva Suesca para el sábado a nombre de Laura.

- **`requestId`**: `25873779-27e1-4fc2-bbd6-0cd85cf8ca1c`
- **`status`**: `ok`
- **`elapsedMs`**: `34122` ms (34.12 s)

**Respuesta (`answer`):**

```text
El costo total del plan para Suesca para 3 personas es $210.000. Con un presupuesto de $250.000, alcanza y sobra $40.000. El clima para el sábado es soleado, con una temperatura entre 7 °C y 21 °C y una probabilidad de lluvia del 15 %. Recomiendo llevar ropa ligera y probablemente un impermeable. El código de reserva es LAR123.
```

**JSON crudo:**

```json
{
  "requestId": "25873779-27e1-4fc2-bbd6-0cd85cf8ca1c",
  "answer": "El costo total del plan para Suesca para 3 personas es $210.000. Con un presupuesto de $250.000, alcanza y sobra $40.000. El clima para el sábado es soleado, con una temperatura entre 7 °C y 21 °C y una probabilidad de lluvia del 15 %. Recomiendo llevar ropa ligera y probablemente un impermeable. El código de reserva es LAR123.",
  "elapsedMs": 34122,
  "status": "ok"
}
```

### P5: Somos 3 con 150000. Si nos alcanza, reserva Suesca el domingo a nombre de Laura.

- **`requestId`**: `385047d6-285d-40f8-b3cd-6da8167b7a0f`
- **`status`**: `ok`
- **`elapsedMs`**: `24459` ms (24.46 s)

**Respuesta (`answer`):**

```text
No se pudo crear la reserva para Suesca el domingo. La condición climática no se cumple y el destino está completo. Te recomiendo revisar los otros destinos: Guatavita, Zipaquirá, Villa de Leyva, Suesca y Chicaque. Estos lugares están disponibles y podrían ser una excelente opción para tu próximo fin de semana.
```

**JSON crudo:**

```json
{
  "requestId": "385047d6-285d-40f8-b3cd-6da8167b7a0f",
  "answer": "No se pudo crear la reserva para Suesca el domingo. La condición climática no se cumple y el destino está completo. Te recomiendo revisar los otros destinos: Guatavita, Zipaquirá, Villa de Leyva, Suesca y Chicaque. Estos lugares están disponibles y podrían ser una excelente opción para tu próximo fin de semana.",
  "elapsedMs": 24459,
  "status": "ok"
}
```

### P6: ¿Quién ganó el mundial de 2022?

- **`requestId`**: `25e19d4f-1e5f-4e76-8ccb-96cfa5b96166`
- **`status`**: `ok`
- **`elapsedMs`**: `11248` ms (11.25 s)

**Respuesta (`answer`):**

```text
No hay resultados para la pregunta sobre quién ganó el Mundial de 2022. Sin embargo, puedo ayudarte con reservas para tus salidas de fin de semana a Guatavita, Zipaquirá, Villa de Leyva, Suesca y Chicaque.
```

**JSON crudo:**

```json
{
  "requestId": "25e19d4f-1e5f-4e76-8ccb-96cfa5b96166",
  "answer": "No hay resultados para la pregunta sobre quién ganó el Mundial de 2022. Sin embargo, puedo ayudarte con reservas para tus salidas de fin de semana a Guatavita, Zipaquirá, Villa de Leyva, Suesca y Chicaque.",
  "elapsedMs": 11248,
  "status": "ok"
}
```
