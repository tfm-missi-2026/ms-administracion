-- =====================================================================
-- Sistema      : SPSRT - Sistema de Planificacion y Seguimiento de Recursos Tecnicos
-- Modulo       : MS Administracion
-- Objetivo     : Cargar los usuarios de desarrollo/mock del FE para que
--                se pueda autenticar contra el backend real sin pasar
--                por el `mock-users.ts` del frontend. Mantiene paridad
--                con los 3 usuarios hardcoded en `tfm-frontend/src/
--                app/core/auth/mock-users.ts` (Pedro Soria, Marcos
--                Pacheco, Edwin Pacheco).
--
-- Contrasena comun : Spsrt.2026 (hasheada con crypt() de pgcrypto,
--                    algoritmo bf=BCrypt, compatible con
--                    BCryptPasswordEncoder de Spring Security).
--
-- Roles asignados:
--   - Pedro Soria    -> ADMIN           (acceso completo)
--   - Marcos Pacheco  -> GESTOR_PROYECTO (proyectos + planificacion)
--   - Edwin Pacheco   -> RECURSO_TECNICO (bitacora + variaciones)
--
-- UUIDs deterministicos (extension del rango seed):
--   admin  existente   00000000-0000-0000-0000-000000000900
--   nuevos mock users  00000000-0000-0000-0000-000000000901..903
-- =====================================================================

INSERT INTO msa_usuario (id, email, contrasenia, nombres, apellido_paterno, apellido_materno, rol_id, fecha_creacion, usuario_creacion) VALUES
('00000000-0000-0000-0000-000000000901',
 'pedro.soria@institucion.gob.pe',
 crypt('Spsrt.2026', gen_salt('bf', 10)),
 'Pedro',
 'Soria',
 '',
 '00000000-0000-0000-0000-000000000001',
 NOW(),
 '00000000-0000-0000-0000-000000000000'),

('00000000-0000-0000-0000-000000000902',
 'marcos.pacheco@institucion.gob.pe',
 crypt('Spsrt.2026', gen_salt('bf', 10)),
 'Marcos',
 'Pacheco',
 '',
 '00000000-0000-0000-0000-000000000003',
 NOW(),
 '00000000-0000-0000-0000-000000000000'),

('00000000-0000-0000-0000-000000000903',
 'edwin.pacheco@institucion.gob.pe',
 crypt('Spsrt.2026', gen_salt('bf', 10)),
 'Edwin',
 'Pacheco',
 '',
 '00000000-0000-0000-0000-000000000004',
 NOW(),
 '00000000-0000-0000-0000-000000000000');
