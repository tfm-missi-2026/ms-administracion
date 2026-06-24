# ms-administracion

Microservicio de **Administración** del sistema SPSRT (UNIR, MISSI): usuarios, roles,
módulos del sidebar, catálogo unificado y **autenticación/JWT** (es el emisor de los tokens).

Parte del sistema **SPSRT — Sistema de Planificación y Seguimiento de Recursos Técnicos**.
El stack completo se orquesta desde el repo
[`orquestacion`](https://github.com/tfm-missi-2026/orquestacion).

## Datos del servicio

| | |
|---|---|
| Puerto | **8081** |
| Base de datos | `spsrt_administracion` (PostgreSQL 16) |
| Prefijo de tablas | `msa_` |
| Paquete base | `pe.unir.tfm.srp.administracion` |
| Stack | Java 21 · Spring Boot 3.5.14 · MyBatis · Flyway · Eureka client |

## URLs útiles (con el stack completo levantado)

- Eureka dashboard — http://localhost:8761
- API Gateway — http://localhost:8080 (`/actuator/health`)
- Swagger — ms-administracion `:8081` · ms-proyectos `:8082` · ms-seguimiento `:8083` (`/swagger-ui.html`)

## Requisitos

- **Docker Desktop 24+** con docker compose v2 (forma recomendada), **o**
- Java 21 + Maven 3.9+ para compilar/ejecutar fuera de contenedor.

## Levantar standalone (solo este servicio + su PostgreSQL)

```bash
cp .env.example .env
docker compose up -d --build
```

Arranca el microservicio + un PostgreSQL propio (sin Eureka). La BD y el usuario se crean
solos; Flyway aplica `V1__init.sql` (esquema) y `V2__seed.sql` (usuarios, roles y módulos
iniciales), así que el **login funciona sin pasos extra**.

- API:     http://localhost:8081
- Health:  http://localhost:8081/actuator/health
- Swagger: http://localhost:8081/swagger-ui.html
- Login:   `POST /api/auth/login` (usar un usuario del seed `V2__seed.sql`)

> Es el **emisor del JWT** que validan el gateway y los demás microservicios; todos deben
> compartir el mismo `JWT_SECRET`.

## Desarrollo local en IntelliJ (perfil `dev`)

Para iterar rápido sin rebuildear la imagen Docker, corré **solo este servicio** desde
IntelliJ (▶ Run de la clase `@SpringBootApplication`) y dejá su PostgreSQL en Docker:

```bash
cd ../../orquestacion && docker compose up -d postgres
```

El perfil **`dev`** desactiva la exigencia de JWT e inyecta un usuario simulado (admin del
seed, con todos los roles), así probás los endpoints protegidos **sin token**. Activalo de una
de estas formas:

- **Run Configuration** → campo *Active profiles*: `dev`, **o**
- VM options: `-Dspring.profiles.active=dev`, **o**
- Variable de entorno: `SPRING_PROFILES_ACTIVE=dev`

Con el perfil activo, pegale directo: `GET http://localhost:8081/api/usuarios` (sin header
`Authorization`) → responde `200`.

> ⚠️ El perfil `dev` **solo** aplica corriendo así. En Docker / `orquestacion` / producción
> (sin perfil) la seguridad JWT sigue intacta.

## Dentro del stack completo

Para correrlo junto a Eureka, el gateway y los demás microservicios, usa el repo
[`orquestacion`](https://github.com/tfm-missi-2026/orquestacion). Allí entra por el gateway
(`http://localhost:8080/api/...`).

## Endpoints principales

| Recurso | Ruta |
|---|---|
| Auth (login) | `/api/auth` |
| Usuarios | `/api/usuarios` |
| Roles | `/api/roles` |
| Módulos | `/api/modulos` |
| Catálogo | `/api/catalogo` |

## Migraciones

`src/main/resources/db/migration/V1__init.sql` + `V2__seed.sql` (Flyway, se aplican al
arrancar). El `V1` instala la extensión `pgcrypto` necesaria para `gen_random_uuid()` y el
hash de contraseñas del seed.
