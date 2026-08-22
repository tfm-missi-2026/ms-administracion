-- =====================================================================
-- Sistema      : SPSRT - Sistema de Planificacion y Seguimiento de Recursos Tecnicos
-- Modulo       : MS Administracion
-- Objetivo     : Agrega a msa_rol la referencia a la pagina de inicio que
--                se muestra tras el login. Almacena un modulo (msa_modulo.id)
--                y el frontend resuelve codigo -> path via MODULO_REGISTRY.
--                La columna es NULLABLE en BD: la obligatoriedad se enforcea
--                en la API (@NotNull) y en la UI (select requerido) para que
--                ningun rol quede sin pagina de inicio.
-- Desarrollado : Equipo SPSRT - UNIR
-- Fecha        : 2026-08-21
-- =====================================================================

ALTER TABLE msa_rol
    ADD COLUMN pagina_inicio_id UUID REFERENCES msa_modulo(id);

-- Seeds para los 4 roles base (editable desde la UI de Roles).
UPDATE msa_rol SET pagina_inicio_id = (SELECT id FROM msa_modulo WHERE codigo = 'INICIO')           WHERE codigo = 'ADMIN';
UPDATE msa_rol SET pagina_inicio_id = (SELECT id FROM msa_modulo WHERE codigo = 'INICIO')           WHERE codigo = 'GESTOR_PROYECTO';
UPDATE msa_rol SET pagina_inicio_id = (SELECT id FROM msa_modulo WHERE codigo = 'DASHBOARD_JEFE')   WHERE codigo = 'JEFE_AREA';
UPDATE msa_rol SET pagina_inicio_id = (SELECT id FROM msa_modulo WHERE codigo = 'DASHBOARD_RECURSO') WHERE codigo = 'RECURSO_TECNICO';

CREATE INDEX idx_msa_rol_pagina_inicio ON msa_rol(pagina_inicio_id);

COMMENT ON COLUMN msa_rol.pagina_inicio_id IS 'Modulo que ve el usuario de este rol tras el login (nullable en BD; obligatorio por API/UI). El frontend lo resuelve con MODULO_REGISTRY (codigo -> path).';