-- =====================================================================
-- Sistema      : SPSRT - Sistema de Planificacion y Seguimiento de Recursos Tecnicos
-- Modulo       : MS Administracion
-- Objetivo     : Agrega los modulos faltantes del area de seguimiento
--                (dashboards por rol, carga y avance) dentro de la seccion
--                SEGUIMIENTO. Antes eran rutas del SPA sin modulo ni RBAC;
--                ahora se pueden asignar por rol, aparecen en el sidebar y
--                son guardables en el frontend.
-- Desarrollado : Equipo SPSRT - UNIR
-- Fecha        : 2026-08-21
-- =====================================================================
--
-- UUIDs deterministas:
--   Modulos nuevos: 00000000-0000-0000-0000-000000000141..144
--   Seccion SEGUIMIENTO: 00000000-0000-0000-0000-000000000202
-- =====================================================================

INSERT INTO msa_modulo (id, codigo, nombre, icono, orden, modulo_padre_id,
                        seccion, sistema, tipo, descripcion,
                        estado, fecha_creacion, usuario_creacion) VALUES
('00000000-0000-0000-0000-000000000141', 'DASHBOARD_RECURSO', 'Dashboard del Recurso Tecnico', 'user',   4, '00000000-0000-0000-0000-000000000202', 'SEGUIMIENTO', 1, 'MENU', 'Vista del recurso: su bitacora y sus tareas asignadas', 1, NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000142', 'CARGA_EQUIPO',      'Carga del equipo',             'users',  5, '00000000-0000-0000-0000-000000000202', 'SEGUIMIENTO', 1, 'MENU', 'Carga y utilizacion del equipo de desarrollo',              1, NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000143', 'AVANCE',            'Avance',                        'check',  6, '00000000-0000-0000-0000-000000000202', 'SEGUIMIENTO', 1, 'MENU', 'Avance por proyecto y por tarea vs linea base',              1, NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000144', 'DASHBOARD_JEFE',    'Dashboard del Jefe de Area',   'home',   7, '00000000-0000-0000-0000-000000000202', 'SEGUIMIENTO', 1, 'MENU', 'Vista del jefe de area: carga, avance y variaciones',        1, NOW(), '00000000-0000-0000-0000-000000000000');

-- RBAC de los nuevos modulos:
--   ADMIN            -> todos
--   JEFE_AREA        -> DASHBOARD_JEFE, CARGA_EQUIPO, AVANCE
--   GESTOR_PROYECTO  -> CARGA_EQUIPO, AVANCE
--   RECURSO_TECNICO  -> DASHBOARD_RECURSO
INSERT INTO msa_rol_modulo (rol_id, modulo_id, estado,
                            fecha_creacion, usuario_creacion)
SELECT r.id, m.id, 1, NOW(), '00000000-0000-0000-0000-000000000000'
  FROM msa_rol r
  JOIN msa_modulo m ON m.codigo IN ('DASHBOARD_RECURSO', 'CARGA_EQUIPO', 'AVANCE', 'DASHBOARD_JEFE')
 WHERE r.codigo = 'ADMIN';

INSERT INTO msa_rol_modulo (rol_id, modulo_id, estado,
                            fecha_creacion, usuario_creacion)
SELECT r.id, m.id, 1, NOW(), '00000000-0000-0000-0000-000000000000'
  FROM msa_rol r
  JOIN msa_modulo m ON m.codigo IN ('DASHBOARD_JEFE', 'CARGA_EQUIPO', 'AVANCE')
 WHERE r.codigo IN ('JEFE_AREA', 'GESTOR_PROYECTO');

INSERT INTO msa_rol_modulo (rol_id, modulo_id, estado,
                            fecha_creacion, usuario_creacion)
SELECT r.id, m.id, 1, NOW(), '00000000-0000-0000-0000-000000000000'
  FROM msa_rol r
  JOIN msa_modulo m ON m.codigo = 'DASHBOARD_RECURSO'
 WHERE r.codigo = 'RECURSO_TECNICO';