-- =====================================================================
-- Sistema      : SPSRT - Sistema de Planificacion y Seguimiento de Recursos Tecnicos
-- Modulo       : MS Administracion
-- Objetivo     : Unifica las secciones del sidebar dentro de msa_modulo
--                y elimina la tabla msa_seccion. Cada fila de msa_modulo
--                lleva un `tipo`:
--                  * SECTION   -> cabecera del sidebar (la seccion misma)
--                  * MENU      -> item plano dentro de una seccion
--                  * SUBMENU   -> item reservado para jerarquia futura
--                                 (se guarda el tipo, el sidebar lo trata
--                                 igual que MENU por ahora).
--                La seccion de un modulo es el modulo SECTION que queda
--                referenciado por `modulo_padre_id`. El titulo y orden de
--                la seccion se resuelven por JOIN contra ese padre.
-- Desarrollado : Equipo SPSRT - UNIR
-- Fecha        : 2026-08-21
-- =====================================================================
--
-- UUIDs deterministas:
--   Secciones (mismos ids que tenia msa_seccion):
--     OPERACION      ...201
--     SEGUIMIENTO    ...202
--     ADMINISTRACION ...203
--     CUENTA         ...204
--   Modulo contenedor ADMINISTRACION (V2 ...107) se deshabilita: su rol
--     lo reemplaza la seccion SECTION ADMINISTRACION.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Bloque 1: Columna `tipo` en msa_modulo
-- ---------------------------------------------------------------------
ALTER TABLE msa_modulo
    ADD COLUMN tipo VARCHAR(20) NOT NULL DEFAULT 'MENU'
        CHECK (tipo IN ('SECTION', 'MENU', 'SUBMENU'));

COMMENT ON COLUMN msa_modulo.tipo IS 'Tipo del modulo: SECTION=cabecera de sidebar, MENU=item plano, SUBMENU=item reservado para jerarquia futura.';

-- ---------------------------------------------------------------------
-- Bloque 2: Secciones como filas de msa_modulo (tipo SECTION)
-- ---------------------------------------------------------------------
INSERT INTO msa_modulo (id, codigo, nombre, icono, orden, modulo_padre_id,
                        seccion, sistema, tipo, descripcion,
                        estado, fecha_creacion, usuario_creacion) VALUES
('00000000-0000-0000-0000-000000000201', 'OPERACION',      'Operacion',      NULL, 1, NULL, 'OPERACION',      1, 'SECTION', 'Seccion Operacion del sidebar',      1, NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000202', 'SEGUIMIENTO',    'Seguimiento',    NULL, 2, NULL, 'SEGUIMIENTO',    1, 'SECTION', 'Seccion Seguimiento del sidebar',    1, NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000203', 'SECCION_ADMINISTRACION', 'Administracion', NULL, 3, NULL, 'ADMINISTRACION', 1, 'SECTION', 'Seccion Administracion del sidebar', 1, NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000204', 'CUENTA',         'Mi cuenta',      NULL, 4, NULL, 'CUENTA',         1, 'SECTION', 'Seccion Mi cuenta del sidebar',      1, NOW(), '00000000-0000-0000-0000-000000000000');

-- ---------------------------------------------------------------------
-- Bloque 3: Reasignar menues a su seccion (modulo_padre_id -> SECTION)
-- ---------------------------------------------------------------------
UPDATE msa_modulo SET tipo = 'MENU',     modulo_padre_id = '00000000-0000-0000-0000-000000000201'
 WHERE codigo IN ('INICIO', 'PROYECTOS', 'ASIGNACIONES', 'VARIACIONES', 'LINEA_BASE');
UPDATE msa_modulo SET tipo = 'MENU',     modulo_padre_id = '00000000-0000-0000-0000-000000000202'
 WHERE codigo = 'BITACORA';
UPDATE msa_modulo SET tipo = 'MENU',     modulo_padre_id = '00000000-0000-0000-0000-000000000203'
 WHERE codigo IN ('USUARIOS', 'ROLES', 'CATALOGO', 'MODULOS');
UPDATE msa_modulo SET tipo = 'MENU',     modulo_padre_id = '00000000-0000-0000-0000-000000000204'
 WHERE codigo = 'CONFIGURACION';

-- Submenues de Proyectos: NO existen submenues en el sidebar actual
-- (todo es plano). Estos tres modulos no tienen pagina propia en el
-- frontend (no estan en el registry de rutas) y no se muestran como
-- items; se deshabilitan y su navegacion queda cubierta por el modulo
-- PROYECTOS.
UPDATE msa_modulo
   SET estado = 0,
       fecha_eliminacion = NOW(),
       usuario_eliminacion = '00000000-0000-0000-0000-000000000000',
       motivo_eliminacion = 'Sin pagina propia; su navegacion la cubre el modulo PROYECTOS (V5)'
 WHERE codigo IN ('SISTEMAS', 'SUBPROYECTOS', 'TAREAS');

-- ---------------------------------------------------------------------
-- Bloque 4: Deshabilitar el contenedor ADMINISTRACION (V2 ...107)
-- ---------------------------------------------------------------------
-- Su rol de agrupador lo cumple ahora la seccion SECTION ADMINISTRACION.
UPDATE msa_modulo
   SET estado = 0,
       fecha_eliminacion = NOW(),
       usuario_eliminacion = '00000000-0000-0000-0000-000000000000',
       motivo_eliminacion = 'Reemplazado por la seccion SECTION ADMINISTRACION (V5)'
 WHERE id = '00000000-0000-0000-0000-000000000107';

-- ---------------------------------------------------------------------
-- Bloque 5: RBAC de las secciones (todas las secciones a todos los roles)
-- ---------------------------------------------------------------------
-- El sidebar pide `GET /api/modulos/por-rol/{rolId}`; necesita recibir
-- las secciones (SECTION) para agrupar. Las secciones sin items visibles
-- las omite el builder del frontend.
INSERT INTO msa_rol_modulo (rol_id, modulo_id, estado,
                            fecha_creacion, usuario_creacion)
SELECT r.id, s.id, 1, NOW(), '00000000-0000-0000-0000-000000000000'
  FROM msa_rol r
 CROSS JOIN msa_modulo s
 WHERE s.tipo = 'SECTION'
   AND r.codigo IN ('ADMIN', 'JEFE_AREA', 'GESTOR_PROYECTO', 'RECURSO_TECNICO');

-- ---------------------------------------------------------------------
-- Bloque 6: Eliminar msa_seccion
-- ---------------------------------------------------------------------
DROP TABLE msa_seccion;