# Planeador de fin de semana multiagente (Spring AI + Ollama)

Sistema multiagente de planificación de salidas de fin de semana cerca de Bogotá construido con **Spring Boot 4.1**, **Spring AI 2.0**, **Ollama (`qwen2.5:3b`)** y un frontend web con panel de actividad en tiempo real.

## Arquitectura de Agentes

Un agente **Supervisor** coordina a cuatro agentes especialistas que invocan herramientas (`@Tool`) con datos simulados en memoria (`DatosPlaneador`), y finalmente un **Sintetizador** redacta la respuesta para el usuario:

| Componente | Tipo | Responsabilidad | Herramientas (`@Tool`) |
|---|---|---|---|
| **Supervisor** | `ChatClient` + salida estructurada (`Decision`) | Decide qué especialista debe actuar a continuación o si finaliza (`FIN`) | Ninguna |
| **Agente de Clima** | `ChatClient` + `ClimaTools` | Consulta pronósticos del sábado o domingo | `listarDestinos`, `consultarPronostico` |
| **Agente de Presupuesto** | `ChatClient` + `PresupuestoTools` | Calcula costos grupales y verifica si alcanza el presupuesto | `estimarCosto`, `calcularSaldo` |
| **Agente de Reservas** | `ChatClient` + `ReservasTools` | Consulta disponibilidad de cupos y registra reservas | `consultarDisponibilidad`, `crearReserva` |
| **Agente de Actividades** *(Reto Parte 7)* | `ChatClient` + `ActividadesTools` | Recomienda 3 planes o actividades turísticas por destino | `recomendarActividades` |
| **Sintetizador** | `ChatClient` | Redacta la respuesta final a partir de los hallazgos de los especialistas | Ninguna |

- **Límite de autonomía (`MAX_PASOS`)**: `4` especialistas como máximo por consulta, sin repetir agentes ya consultados.

## Reto Parte 7 Implementado: Agente de Actividades

Se extendió el sistema multiagente con un cuarto especialista (`ACTIVIDADES`):

1. **`DatosPlaneador.java`**: Mapa `actividades` con 3 planes simulados para cada uno de los 5 destinos (`guatavita`, `zipaquira`, `villa de leyva`, `suesca`, `chicaque`) y método `actividades(String clave)`.
2. **`ActividadesTools.java`**: Componente `@Component` con la herramienta `@Tool recomendarActividades(@ToolParam String destino)` que resuelve el destino con `datos.buscarDestino(destino)` y registra el evento en `Progreso`.
3. **`AgentesConfig.java`**: Definición de `PROMPT_ACTIVIDADES`, bean `@Bean ChatClient agenteActividades(...)` e inclusión de `ACTIVIDADES` en `PROMPT_SUPERVISOR` y `PROMPT_SINTETIZADOR`.
4. **`Decision.java`**: Inclusión de `ACTIVIDADES` en `enum Agente { CLIMA, PRESUPUESTO, RESERVAS, ACTIVIDADES, FIN }`.
5. **`PlaneadorOrquestador.java`**: Inyección de `@Qualifier("agenteActividades") ChatClient`, registro en el mapa `especialistas` y actualización de `MAX_PASOS = 4`.
6. **`HerramientasTest.java`**: Prueba unitaria `recomiendaActividadesDeVillaDeLeyva()` validando `ActividadesTools`.

## Pasos de Ejecución

### 1. Ejecutar pruebas unitarias (JUnit)

Si tienes Gradle instalado localmente:

```bash
cd backend
gradle test
```

O usando el contenedor oficial de Gradle con JDK 21:

```bash
docker run --rm -v "${PWD}/backend:/home/gradle/project" -w /home/gradle/project gradle:9-jdk21 gradle test --no-daemon
```

### 2. Levantar el stack con Docker Compose

Desde la raíz de `pruebachat/`:

```bash
docker compose -f docker/docker-compose.yml up -d --build
```

Servicios desplegados:
- **`pruebachat_ollama`** (`localhost:11434`): Servidor Ollama.
- **`pruebachat_ollama_pull`**: Descarga automáticamente el modelo `qwen2.5:3b`.
- **`pruebachat_backend`** (`localhost:8080`): Backend Spring Boot + Spring AI.
- **`pruebachat_frontend`** (`localhost:4200`): Interfaz web con panel de actividad de agentes.

### 3. Validación y pruebas de endpoints

Verificar salud del modelo LLM:

```bash
curl http://localhost:8080/health/llm
```

Probar un agente individual desde el endpoint de depuración:

```bash
curl "http://localhost:8080/api/v1/debug/presupuesto?q=Somos%204%20con%20350000%20pesos,%20alcanza%20para%20Zipaquira"
curl "http://localhost:8080/api/v1/debug/actividades?q=Que%20hacer%20en%20Villa%20de%20Leyva"
```

Consultar al orquestador completo:

```bash
curl -X POST http://localhost:8080/api/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Qué actividades recomiendas en Villa de Leyva y cuánto cuesta para 2 personas?"}'
```

## Evidencias y Documentación (`docs/`)

- `docs/junit_output.txt`: Salida de las pruebas unitarias JUnit (`HerramientasTest`, 6 pruebas incluyendo `ActividadesTools`).
- `docs/resultados_springai.md`: Respuestas exactas, `status`, `elapsedMs` y tiempo promedio de las 6 preguntas base.
- `docs/traza_springai.txt`: Traza completa de logs del contenedor `pruebachat_backend` durante las 6 preguntas base.
- `docs/evidencia_reto_springai.txt`: Respuesta y logs de ejecución del Reto Parte 7 (`ACTIVIDADES` + `PRESUPUESTO`).
