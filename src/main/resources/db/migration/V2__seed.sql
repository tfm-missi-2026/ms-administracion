-- =====================================================================
-- Sistema      : SPSRT - Sistema de Planificacion y Seguimiento de Recursos Tecnicos
-- Modulo       : MS Administracion
-- Objetivo     : Datos semilla del MS Administracion. Carga 19 modulos
--                del sidebar (4 secciones y 15 menus), 4 roles con su
--                pagina de inicio, 41 asignaciones rol-modulo (RBAC),
--                23 items del catalogo unificado y 5 usuarios de
--                desarrollo.
-- Desarrollado : Equipo SPSRT - UNIR
-- Fecha        : 2026-11-03
-- =====================================================================
--
-- Constantes:
--   SYSTEM_USER_UUID = '00000000-0000-0000-0000-000000000000'
--     Se usa como usuario_creacion del seed (no es un usuario real).
--
-- UUIDs deterministicos:
--   Roles:      00000000-0000-0000-0000-00000000000X
--   Modulos:    00000000-0000-0000-0000-0000000001XX
--   Secciones:  00000000-0000-0000-0000-00000000020X
--   Usuarios:   00000000-0000-0000-0000-000000000900..904
--
-- Contrasenas (hasheadas con crypt() de pgcrypto, algoritmo bf=BCrypt,
-- compatible con BCryptPasswordEncoder de Spring Security):
--   admin@srp.local          : Admin123
--   usuarios @institucion    : Spsrt.2026
-- =====================================================================

-- ---------------------------------------------------------------------
-- Secciones del sidebar (modulos tipo SECTION)
-- ---------------------------------------------------------------------
INSERT INTO msa_modulo (id, codigo, nombre, icono, orden, modulo_padre_id, tipo, seccion, sistema, descripcion, fecha_creacion, usuario_creacion) VALUES
('00000000-0000-0000-0000-000000000201', 'OPERACION',              'Operacion',      NULL, 1, NULL, 'SECTION', 'OPERACION',      1, 'Seccion Operacion del sidebar',      NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000202', 'SEGUIMIENTO',            'Seguimiento',    NULL, 2, NULL, 'SECTION', 'SEGUIMIENTO',    1, 'Seccion Seguimiento del sidebar',    NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000203', 'SECCION_ADMINISTRACION', 'Administracion', NULL, 3, NULL, 'SECTION', 'ADMINISTRACION', 1, 'Seccion Administracion del sidebar', NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000204', 'CUENTA',                 'Mi cuenta',      NULL, 4, NULL, 'SECTION', 'CUENTA',         1, 'Seccion Mi cuenta del sidebar',      NOW(), '00000000-0000-0000-0000-000000000000');

-- ---------------------------------------------------------------------
-- Menus del sidebar (modulos tipo MENU)
-- ---------------------------------------------------------------------
INSERT INTO msa_modulo (id, codigo, nombre, icono, orden, modulo_padre_id, tipo, seccion, sistema, descripcion, fecha_creacion, usuario_creacion) VALUES
-- OPERACION
('00000000-0000-0000-0000-000000000101', 'INICIO',            'Inicio',                        'home',     1, '00000000-0000-0000-0000-000000000201', 'MENU', 'OPERACION',      1, 'Dashboard principal con indicadores generales',                                           NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000102', 'PROYECTOS',         'Proyectos',                     'folder',   2, '00000000-0000-0000-0000-000000000201', 'MENU', 'OPERACION',      1, 'Gestion de sistemas, subproyectos y tareas',                                              NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000104', 'ASIGNACIONES',      'Asignaciones',                  'users',    4, '00000000-0000-0000-0000-000000000201', 'MENU', 'OPERACION',      1, 'Asignacion de recursos tecnicos a tareas',                                                NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000105', 'VARIACIONES',       'Variaciones',                   'edit',     5, '00000000-0000-0000-0000-000000000201', 'MENU', 'OPERACION',      1, 'Registro y resolucion de variaciones (alcance, plazo, recursos)',                         NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000106', 'LINEA_BASE',        'Linea Base',                    'archive',  6, '00000000-0000-0000-0000-000000000201', 'MENU', 'OPERACION',      1, 'Snapshots congelados del plan y comparativa contra estado vigente',                       NOW(), '00000000-0000-0000-0000-000000000000'),
-- SEGUIMIENTO
('00000000-0000-0000-0000-000000000103', 'BITACORA',          'Mi Bitacora',                   'journal',  3, '00000000-0000-0000-0000-000000000202', 'MENU', 'SEGUIMIENTO',    1, 'Registro diario de tiempo del recurso (trabajo en tareas + actividades transversales)',  NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000141', 'DASHBOARD_RECURSO', 'Dashboard del Recurso Tecnico', 'user',     4, '00000000-0000-0000-0000-000000000202', 'MENU', 'SEGUIMIENTO',    1, 'Vista del recurso: su bitacora y sus tareas asignadas',                                   NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000142', 'CARGA_EQUIPO',      'Carga del equipo',              'users',    5, '00000000-0000-0000-0000-000000000202', 'MENU', 'SEGUIMIENTO',    1, 'Carga y utilizacion del equipo de desarrollo',                                            NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000143', 'AVANCE',            'Avance',                        'check',    6, '00000000-0000-0000-0000-000000000202', 'MENU', 'SEGUIMIENTO',    1, 'Avance por proyecto y por tarea vs linea base',                                           NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000144', 'DASHBOARD_JEFE',    'Dashboard del Jefe de Area',    'home',     7, '00000000-0000-0000-0000-000000000202', 'MENU', 'SEGUIMIENTO',    1, 'Vista del jefe de area: carga, avance y variaciones',                                     NOW(), '00000000-0000-0000-0000-000000000000'),
-- ADMINISTRACION
('00000000-0000-0000-0000-000000000121', 'USUARIOS',          'Usuarios',                      'user',     1, '00000000-0000-0000-0000-000000000203', 'MENU', 'ADMINISTRACION', 1, 'Alta, edicion y baja de usuarios del sistema',                                            NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000122', 'ROLES',             'Roles',                         'shield',   2, '00000000-0000-0000-0000-000000000203', 'MENU', 'ADMINISTRACION', 1, 'Definicion de roles del sistema',                                                         NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000123', 'CATALOGO',          'Catalogo',                      'list',     3, '00000000-0000-0000-0000-000000000203', 'MENU', 'ADMINISTRACION', 1, 'Mantenimiento del catalogo unificado (tipos, prioridades, situaciones)',                  NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000124', 'MODULOS',           'Modulos',                       'grid',     4, '00000000-0000-0000-0000-000000000203', 'MENU', 'ADMINISTRACION', 1, 'Mantenimiento de modulos del sidebar y asignacion por rol',                               NOW(), '00000000-0000-0000-0000-000000000000'),
-- CUENTA
('00000000-0000-0000-0000-000000000131', 'CONFIGURACION',     'Configuracion',                 'settings', 1, '00000000-0000-0000-0000-000000000204', 'MENU', 'CUENTA',         1, 'Preferencias del usuario y datos de sesion',                                              NOW(), '00000000-0000-0000-0000-000000000000');

-- ---------------------------------------------------------------------
-- Roles
-- ---------------------------------------------------------------------
INSERT INTO msa_rol (id, codigo, nombre, descripcion, sistema, pagina_inicio_id, fecha_creacion, usuario_creacion) VALUES
('00000000-0000-0000-0000-000000000001', 'ADMIN',           'Administrador del Sistema', 'Acceso completo a la administracion (usuarios, roles, modulos, catalogo)',                          1, '00000000-0000-0000-0000-000000000121', NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000002', 'JEFE_AREA',       'Jefe del Area',             'Visualiza la carga y avance del area de desarrollo; aprueba variaciones a alto nivel',               1, '00000000-0000-0000-0000-000000000144', NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000003', 'GESTOR_PROYECTO', 'Gestor de Proyecto',        'Gestiona proyectos, subproyectos, tareas, asignaciones, congela linea base y resuelve variaciones', 1, '00000000-0000-0000-0000-000000000101', NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000004', 'RECURSO_TECNICO', 'Recurso Tecnico',           'Registra su bitacora diaria de trabajo y reporta variaciones que afecten sus tareas asignadas',      1, '00000000-0000-0000-0000-000000000141', NOW(), '00000000-0000-0000-0000-000000000000');

-- ---------------------------------------------------------------------
-- Asignaciones rol-modulo (RBAC del sidebar)
-- ---------------------------------------------------------------------
-- ADMIN: solo administracion y configuracion (7 filas)
INSERT INTO msa_rol_modulo (rol_id, modulo_id, fecha_creacion, usuario_creacion)
SELECT '00000000-0000-0000-0000-000000000001', m.id, NOW(), '00000000-0000-0000-0000-000000000000'
FROM msa_modulo m
WHERE m.codigo IN ('SECCION_ADMINISTRACION', 'CUENTA', 'USUARIOS', 'ROLES', 'CATALOGO', 'MODULOS', 'CONFIGURACION');

-- JEFE_AREA: secciones, operacion, carga, avance y su dashboard (13 filas)
INSERT INTO msa_rol_modulo (rol_id, modulo_id, fecha_creacion, usuario_creacion)
SELECT '00000000-0000-0000-0000-000000000002', m.id, NOW(), '00000000-0000-0000-0000-000000000000'
FROM msa_modulo m
WHERE m.tipo = 'SECTION'
   OR m.codigo IN ('INICIO', 'PROYECTOS', 'ASIGNACIONES', 'VARIACIONES', 'LINEA_BASE',
                   'CARGA_EQUIPO', 'AVANCE', 'DASHBOARD_JEFE', 'CONFIGURACION');

-- GESTOR_PROYECTO: secciones, operacion, carga y avance (12 filas)
INSERT INTO msa_rol_modulo (rol_id, modulo_id, fecha_creacion, usuario_creacion)
SELECT '00000000-0000-0000-0000-000000000003', m.id, NOW(), '00000000-0000-0000-0000-000000000000'
FROM msa_modulo m
WHERE m.tipo = 'SECTION'
   OR m.codigo IN ('INICIO', 'PROYECTOS', 'ASIGNACIONES', 'VARIACIONES', 'LINEA_BASE',
                   'CARGA_EQUIPO', 'AVANCE', 'CONFIGURACION');

-- RECURSO_TECNICO: secciones, inicio, variaciones, bitacora y su dashboard (9 filas)
INSERT INTO msa_rol_modulo (rol_id, modulo_id, fecha_creacion, usuario_creacion)
SELECT '00000000-0000-0000-0000-000000000004', m.id, NOW(), '00000000-0000-0000-0000-000000000000'
FROM msa_modulo m
WHERE m.tipo = 'SECTION'
   OR m.codigo IN ('INICIO', 'VARIACIONES', 'BITACORA', 'DASHBOARD_RECURSO', 'CONFIGURACION');

-- ---------------------------------------------------------------------
-- Catalogo unificado (7 grupos)
-- ---------------------------------------------------------------------
INSERT INTO msa_catalogo (grupo, id_opcion, opcion, fecha_creacion, usuario_creacion) VALUES
-- TIPO_SUBPROYECTO
('TIPO_SUBPROYECTO',    1, 'Incidencia',           NOW(), '00000000-0000-0000-0000-000000000000'),
('TIPO_SUBPROYECTO',    2, 'Requerimiento',        NOW(), '00000000-0000-0000-0000-000000000000'),
('TIPO_SUBPROYECTO',    3, 'Desarrollo modular',   NOW(), '00000000-0000-0000-0000-000000000000'),
-- PRIORIDAD
('PRIORIDAD',           1, 'Alta',                 NOW(), '00000000-0000-0000-0000-000000000000'),
('PRIORIDAD',           2, 'Media',                NOW(), '00000000-0000-0000-0000-000000000000'),
('PRIORIDAD',           3, 'Baja',                 NOW(), '00000000-0000-0000-0000-000000000000'),
-- SITUACION (subproyecto y tarea)
('SITUACION',           1, 'Pendiente',            NOW(), '00000000-0000-0000-0000-000000000000'),
('SITUACION',           2, 'En atencion',          NOW(), '00000000-0000-0000-0000-000000000000'),
('SITUACION',           3, 'Culminado',            NOW(), '00000000-0000-0000-0000-000000000000'),
('SITUACION',           4, 'Rechazado',            NOW(), '00000000-0000-0000-0000-000000000000'),
-- TIPO_VARIACION
('TIPO_VARIACION',      1, 'Alcance',              NOW(), '00000000-0000-0000-0000-000000000000'),
('TIPO_VARIACION',      2, 'Plazo',                NOW(), '00000000-0000-0000-0000-000000000000'),
('TIPO_VARIACION',      3, 'Recursos',             NOW(), '00000000-0000-0000-0000-000000000000'),
-- SITUACION_VARIACION
('SITUACION_VARIACION', 1, 'Pendiente',            NOW(), '00000000-0000-0000-0000-000000000000'),
('SITUACION_VARIACION', 2, 'Aprobada',             NOW(), '00000000-0000-0000-0000-000000000000'),
('SITUACION_VARIACION', 3, 'Rechazada',            NOW(), '00000000-0000-0000-0000-000000000000'),
-- TIPO_ACTIVIDAD
('TIPO_ACTIVIDAD',      1, 'Reunion',              NOW(), '00000000-0000-0000-0000-000000000000'),
('TIPO_ACTIVIDAD',      2, 'Capacitacion',         NOW(), '00000000-0000-0000-0000-000000000000'),
('TIPO_ACTIVIDAD',      3, 'Soporte',              NOW(), '00000000-0000-0000-0000-000000000000'),
('TIPO_ACTIVIDAD',      4, 'Otro',                 NOW(), '00000000-0000-0000-0000-000000000000'),
-- MODALIDAD
('MODALIDAD',           1, 'Presencial',           NOW(), '00000000-0000-0000-0000-000000000000'),
('MODALIDAD',           2, 'Virtual',              NOW(), '00000000-0000-0000-0000-000000000000'),
('MODALIDAD',           3, 'Hibrida',              NOW(), '00000000-0000-0000-0000-000000000000');

-- ---------------------------------------------------------------------
-- Usuarios de desarrollo
-- ---------------------------------------------------------------------
--   Administrador del Sistema -> ADMIN
--   Pedro Soria               -> RECURSO_TECNICO
--   Marcos Pacheco            -> GESTOR_PROYECTO
--   Edwin Pacheco             -> RECURSO_TECNICO
--   Juan Perez                -> JEFE_AREA
INSERT INTO msa_usuario (id, email, contrasenia, nombres, apellido_paterno, apellido_materno, rol_id, fecha_creacion, usuario_creacion) VALUES
('00000000-0000-0000-0000-000000000900', 'admin@srp.local',                   crypt('Admin123',   gen_salt('bf', 10)), 'Administrador', 'del',     'Sistema', '00000000-0000-0000-0000-000000000001', NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000901', 'pedro.soria@institucion.gob.pe',    crypt('Spsrt.2026', gen_salt('bf', 10)), 'Pedro',         'Soria',   '',        '00000000-0000-0000-0000-000000000004', NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000902', 'marcos.pacheco@institucion.gob.pe', crypt('Spsrt.2026', gen_salt('bf', 10)), 'Marcos',        'Pacheco', '',        '00000000-0000-0000-0000-000000000003', NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000903', 'edwin.pacheco@institucion.gob.pe',  crypt('Spsrt.2026', gen_salt('bf', 10)), 'Edwin',         'Pacheco', '',        '00000000-0000-0000-0000-000000000004', NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000904', 'juan.perez@institucion.gob.pe',     crypt('Spsrt.2026', gen_salt('bf', 10)), 'Juan',          'Perez',   '',        '00000000-0000-0000-0000-000000000002', NOW(), '00000000-0000-0000-0000-000000000000');
