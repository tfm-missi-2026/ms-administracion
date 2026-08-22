-- =====================================================================
-- Sistema      : SPSRT - Sistema de Planificacion y Seguimiento de Recursos Tecnicos
-- Modulo       : MS Administracion
-- Objetivo     : Enriquece msa_modulo y msa_rol con metadata necesaria
--                para que el sidebar del frontend se renderice en
--                secciones (sin hardcodes de UI) y para que la UI de
--                Roles y Permisos distinga roles/modulos del sistema
--                de los creados por usuario. Tambien:
--                  * Agrega el modulo CONFIGURACION (seccion Mi Cuenta)
--                    y su asignacion a los 4 roles base del seed.
--                  * Crea el catalogo de secciones UI (msa_seccion) que
--                    aporta titulo y orden al sidebar.
--                Migration UNICA y cohesiva: cubre metadata de modulo +
--                rol + modulo nuevo + catalogo de secciones + indices +
--                comentarios. No hay migrations paralelas.
-- Desarrollado : Equipo SPSRT - UNIR
-- Fecha        : 2026-08-21
-- =====================================================================
--
-- Cambios:
--   msa_modulo
--     + seccion      VARCHAR(40) NOT NULL DEFAULT 'OPERACION'
--     + sistema      SMALLINT    NOT NULL DEFAULT 1
--   msa_rol
--     + sistema      SMALLINT    NOT NULL DEFAULT 0
--   msa_seccion   (tabla nueva)
--     Catalogo de secciones UI del sidebar. Aporta titulo y orden a
--     cada modulo segun su columna `seccion`. El frontend NO define
--     secciones en codigo; las lee de aqui.
--
-- UUIDs deterministicos para los registros core:
--   Roles:                  00000000-0000-0000-0000-00000000000X
--   Modulos:                00000000-0000-0000-0000-00000000010X
--   Modulo CONFIGURACION:   00000000-0000-0000-0000-000000000131
--   Secciones:              00000000-0000-0000-0000-00000000020X
--   Usuario admin:          00000000-0000-0000-0000-000000000900
-- =====================================================================

-- ---------------------------------------------------------------------
-- Bloque 1: Enriquece msa_modulo
-- ---------------------------------------------------------------------
ALTER TABLE msa_modulo
    ADD COLUMN seccion     VARCHAR(40) NOT NULL DEFAULT 'OPERACION',
    ADD COLUMN sistema     SMALLINT    NOT NULL DEFAULT 1;

-- Backfill de secciones para los 14 modulos del seed.
UPDATE msa_modulo SET seccion = 'OPERACION'      WHERE codigo = 'INICIO';
UPDATE msa_modulo SET seccion = 'OPERACION'      WHERE codigo = 'PROYECTOS';
UPDATE msa_modulo SET seccion = 'OPERACION'      WHERE codigo = 'ASIGNACIONES';
UPDATE msa_modulo SET seccion = 'OPERACION'      WHERE codigo = 'VARIACIONES';
UPDATE msa_modulo SET seccion = 'OPERACION'      WHERE codigo = 'LINEA_BASE';
UPDATE msa_modulo SET seccion = 'SEGUIMIENTO'    WHERE codigo = 'BITACORA';
UPDATE msa_modulo SET seccion = 'ADMINISTRACION' WHERE codigo IN ('ADMINISTRACION', 'USUARIOS', 'ROLES', 'CATALOGO', 'MODULOS');
-- Submenus heredan la seccion de su padre (segun V2__seed.sql).
UPDATE msa_modulo SET seccion = 'OPERACION'      WHERE codigo IN ('SISTEMAS', 'SUBPROYECTOS', 'TAREAS');

-- sistema: todos los del seed son del sistema; los nuevos arrancan como custom.
UPDATE msa_modulo SET sistema = 1
 WHERE codigo IN ('INICIO', 'PROYECTOS', 'BITACORA', 'ASIGNACIONES', 'VARIACIONES',
                  'LINEA_BASE', 'ADMINISTRACION', 'SISTEMAS', 'SUBPROYECTOS',
                  'TAREAS', 'USUARIOS', 'ROLES', 'CATALOGO', 'MODULOS');

CREATE INDEX idx_msa_modulo_seccion ON msa_modulo(seccion);
CREATE INDEX idx_msa_modulo_sistema  ON msa_modulo(sistema);

COMMENT ON COLUMN msa_modulo.seccion    IS 'Seccion del sidebar (OPERACION, SEGUIMIENTO, ADMINISTRACION, CUENTA). Default OPERACION.';
COMMENT ON COLUMN msa_modulo.sistema    IS '1 si es modulo del sistema (no editable por UI); 0 si es custom.';

-- ---------------------------------------------------------------------
-- Bloque 2: Enriquece msa_rol
-- ---------------------------------------------------------------------
ALTER TABLE msa_rol
    ADD COLUMN sistema SMALLINT NOT NULL DEFAULT 0;

-- Los 4 roles del seed son del sistema.
UPDATE msa_rol SET sistema = 1
 WHERE codigo IN ('ADMIN', 'JEFE_AREA', 'GESTOR_PROYECTO', 'RECURSO_TECNICO');

COMMENT ON COLUMN msa_rol.sistema IS '1 si es rol del sistema (no editable/eliminable por UI); 0 si es custom.';

-- ---------------------------------------------------------------------
-- Bloque 3: Modulo nuevo CONFIGURACION (seccion CUENTA) + RBAC
-- ---------------------------------------------------------------------
INSERT INTO msa_modulo (id, codigo, nombre, icono, orden, modulo_padre_id,
                       seccion, sistema, descripcion,
                       estado, fecha_creacion, usuario_creacion)
VALUES ('00000000-0000-0000-0000-000000000131',
        'CONFIGURACION', 'Configuracion', 'settings',
        1, NULL,
        'CUENTA', 1,
        'Preferencias del usuario y datos de sesion',
        1, NOW(), '00000000-0000-0000-0000-000000000000');

-- Asignar CONFIGURACION a los 4 roles base del seed.
INSERT INTO msa_rol_modulo (rol_id, modulo_id, estado,
                            fecha_creacion, usuario_creacion)
SELECT r.id, m.id, 1, NOW(), '00000000-0000-0000-0000-000000000000'
  FROM msa_rol r
  JOIN msa_modulo m ON m.codigo = 'CONFIGURACION'
 WHERE r.codigo IN ('ADMIN', 'JEFE_AREA', 'GESTOR_PROYECTO', 'RECURSO_TECNICO');

-- ---------------------------------------------------------------------
-- Bloque 4: Catalogo de secciones UI del sidebar (msa_seccion)
-- ---------------------------------------------------------------------
-- Esta tabla es la fuente de verdad del titulo y orden de cada seccion
-- del sidebar. El frontend lee `msa_modulo.seccion` (string) y luego
-- resuelve titulo/orden con JOIN contra esta tabla. Asi el frontend
-- NO contiene constantes de UI: si se agrega una seccion nueva, se
-- inserta una fila aqui y aparece automaticamente en el sidebar.
CREATE TABLE msa_seccion (
    id                   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo               VARCHAR(40)  NOT NULL UNIQUE,
    titulo               VARCHAR(100) NOT NULL,
    orden                SMALLINT     NOT NULL,
    estado               SMALLINT     NOT NULL DEFAULT 1 CHECK (estado IN (0, 1)),
    fecha_creacion       TIMESTAMP    NOT NULL,
    usuario_creacion     UUID         NOT NULL,
    fecha_modificacion   TIMESTAMP,
    usuario_modificacion UUID,
    fecha_eliminacion    TIMESTAMP,
    usuario_eliminacion  UUID,
    motivo_eliminacion   VARCHAR(500)
);

CREATE INDEX idx_msa_seccion_orden ON msa_seccion(orden);

INSERT INTO msa_seccion (id, codigo, titulo, orden,
                         estado, fecha_creacion, usuario_creacion) VALUES
('00000000-0000-0000-0000-000000000201', 'OPERACION',      'Operacion',      1, 1, NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000202', 'SEGUIMIENTO',    'Seguimiento',    2, 1, NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000203', 'ADMINISTRACION', 'Administracion', 3, 1, NOW(), '00000000-0000-0000-0000-000000000000'),
('00000000-0000-0000-0000-000000000204', 'CUENTA',         'Mi cuenta',      4, 1, NOW(), '00000000-0000-0000-0000-000000000000');

COMMENT ON TABLE  msa_seccion IS 'Catalogo de secciones UI del sidebar. Aporta titulo y orden a partir del codigo que msa_modulo.seccion referencia.';
COMMENT ON COLUMN msa_seccion.id     IS 'Identificador unico de la seccion';
COMMENT ON COLUMN msa_seccion.codigo IS 'Codigo de la seccion (OPERACION, SEGUIMIENTO, ADMINISTRACION, CUENTA). Debe matchear msa_modulo.seccion.';
COMMENT ON COLUMN msa_seccion.titulo IS 'Titulo visible en el sidebar (i18n listo: hoy se guarda en espanol, futuro: tabla de traducciones)';
COMMENT ON COLUMN msa_seccion.orden  IS 'Orden de aparicion de la seccion en el sidebar';