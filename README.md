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
