# FitSync — Backend

Backend de FitSync construido con **Spring Boot 3 + Domain-Driven Design + TDD**,
correspondiente a los Capítulos 3 y 4 del informe del curso Desarrollo Web
Integrado (UTP).

## Versión AvaProy: lo nuevo

Esta carpeta es la versión ampliada. Agrega lo que la landing ofrecía y faltaba:

| Función | Dónde | Detalle |
|---|---|---|
| Inicio de sesión y roles | `login.html`, contexto `identity` | Registro con rol **Atleta** o **Entrenador**, contraseñas con BCrypt y sesión por token (`Authorization: Bearer`). |
| Plan Pro y pagos | `plan.html`, contexto `subscription` | Pasar a Pro (S/ 29 al mes) con una **pasarela simulada**: no pide tarjeta ni cobra dinero real, solo registra la operación (`SIM-…`). El puerto `PaymentGateway` permite cambiarla por una real. |
| Límites del plan | `SupplementCommandHandler` | Con Gratis, un solo plan de suplementación (402 si se intenta otro). |
| Adherencia e historial | `index.html` (panel Hoy), `progreso.html`, contexto `tracking` | El atleta marca entrenamientos y dosis cumplidos; se calcula el % semanal. Historial de 12 semanas solo en Pro. |
| Entrenador y marca blanca | `atletas.html`, contexto `coach` | Un entrenador Pro agrega atletas por correo, ve su semana y su adherencia, y define nombre y color de marca que ven sus atletas. |
| Recordatorios de dosis | panel Hoy de `index.html` | Estado de cada dosis del día (pendiente, atrasada, tomada) y aviso del navegador a la hora de cada una mientras FitSync está abierto. |

**Cuentas de prueba** (se crean al arrancar): `demo@fitsync.com` (atleta) y `entrenador@fitsync.com` (entrenador), ambas con la contraseña `demo1234`.

**Para correr esta versión junto a la original:** usa su propia base (`fitsync-avaproy-db`, puerto 5433) y el puerto **8082**.

```bash
docker compose up -d
mvn spring-boot:run
# http://localhost:8082/login.html
```

## Estructura del proyecto

```
src/main/java/com/fitsync/
├── domain/                  → Reglas de negocio puras (sin Spring, sin JPA)
│   ├── workout/              Aggregate Root: WorkoutProgram
│   └── supplement/            Aggregate Root: SupplementPlan
├── application/              Casos de uso con CQRS (orquesta el dominio)
│   ├── workout/               WorkoutCommandHandler (escritura) + WorkoutQueryHandler (lectura),
│   │                          commands y queries (records)
│   └── supplement/            SupplementCommandHandler + SupplementQueryHandler, commands y queries
├── infrastructure/           Adaptadores técnicos (JPA, Spring Data)
│   ├── workout/               Entidades JPA + WorkoutRepositoryJpaAdapter
│   └── supplement/            Entidades JPA + SupplementRepositoryJpaAdapter
└── interfaces/                Controladores REST, DTOs, manejo global de errores
    ├── workout/                WorkoutController
    ├── supplement/             SupplementController
    └── common/                 GlobalExceptionHandler (@ControllerAdvice)

src/test/java/com/fitsync/
├── domain/workout/            WorkoutProgramTest (JUnit 5, pruebas de dominio)
├── domain/supplement/         SupplementPlanTest
└── application/workout/       WorkoutCommandHandlerTest y WorkoutQueryHandlerTest (JUnit 5 + Mockito)

src/main/resources/
├── application.yml            Configuración (datasource, JPA, Flyway)
├── db/migration/              V1 esquema, V2 usuario demo, V3 columna amount_grams
└── static/                    Pantallas (HTML5/CSS3/JS): index, entrenamiento, suplementos, landing

demo/                         Demostración standalone del ciclo TDD (ver abajo)
```

## Cómo ejecutar el proyecto

**Requisitos:** JDK 21, Maven 3.9+ (o IntelliJ IDEA, que ya trae Maven) y PostgreSQL 16
(la forma más fácil es con Docker).

```bash
# 1. Base de datos (con Docker)
docker compose up -d

# 2. Levantar el backend (Flyway crea las tablas y el usuario demo al arrancar)
mvn spring-boot:run

# 3. Probar la API (el usuario demo ya existe con este UUID)
curl -X POST http://localhost:8081/api/workout-programs \
  -H "Content-Type: application/json" \
  -d '{"userId":"11111111-1111-1111-1111-111111111111","name":"Rutina PPL","splitType":"PUSH_PULL_LEGS"}'
```

La respuesta trae el `id` del programa. Con ese id:

```bash
curl -X POST http://localhost:8081/api/workout-programs/<ID>/days \
  -H "Content-Type: application/json" -d '{"dayOfWeek":"MONDAY"}'

curl -X POST http://localhost:8081/api/workout-programs/<ID>/exercises \
  -H "Content-Type: application/json" \
  -d '{"dayOfWeek":"MONDAY","exerciseName":"Press de banca","muscleGroup":"Pecho","sets":4,"repsPerSet":8,"restSeconds":90}'
```

Repite el último comando tal cual: debe responder **400** con el mensaje de la regla de negocio
(ejercicio duplicado), formateado por `GlobalExceptionHandler`.

## Pantallas (frontend)

Con el backend arrancado, abre en el navegador:

| URL | Pantalla |
|---|---|
| http://localhost:8081/ | **Semana**: tablero que cruza entrenamiento y suplementación (botón "Cargar datos de ejemplo") |
| http://localhost:8081/entrenamiento.html | Crear programas, días y ejercicios; publicar |
| http://localhost:8081/suplementos.html | Crear planes y programar dosis |
| http://localhost:8081/landing.html | Landing page |

Las pantallas viven en `src/main/resources/static`, así que Spring Boot las sirve desde el
mismo puerto que la API (no hace falta configurar CORS).

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/workout-programs?userId=` | Lista programas del usuario |
| POST | `/api/workout-programs` | Crea programa |
| GET | `/api/workout-programs/{id}` | Detalle |
| POST | `/api/workout-programs/{id}/days` · `/exercises` · `/publish` | Agrega día, ejercicio; publica |
| GET | `/api/supplement-plans?userId=` | Lista planes del usuario |
| POST | `/api/supplement-plans` | Crea plan (opcionalmente vinculado a un programa) |
| GET | `/api/supplement-plans/{id}` | Detalle |
| POST | `/api/supplement-plans/{id}/doses` | Programa una dosis |

## Cómo correr las pruebas (JUnit 5)

```bash
mvn test
```

## Demo del ciclo TDD sin Maven (solo JDK)

La carpeta `demo/` contiene una versión standalone (sin JUnit, sin Spring)
de las pruebas del agregado `WorkoutProgram`, pensada para poder
**compilarse y ejecutarse con solo `javac`/`java`**, útil para demostrar
en vivo el ciclo Red-Green-Refactor durante la exposición:

```bash
javac -encoding UTF-8 -d demo/out \
  src/main/java/com/fitsync/domain/workout/*.java \
  demo/MiniTest.java demo/WorkoutProgramLiveDemo.java

java -cp demo/out WorkoutProgramLiveDemo
```

Debe mostrar `3 pruebas en verde, 0 en rojo`.
