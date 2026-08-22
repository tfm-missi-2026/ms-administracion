-- =====================================================================
-- Sistema      : SPSRT - Sistema de Planificacion y Seguimiento de Recursos Tecnicos
-- Modulo       : MS Administracion
-- Objetivo     : Correccion del RBAC de V6: el rol GESTOR_PROYECTO no debe
--                ver el Dashboard del Jefe de Area (DASHBOARD_JEFE). Solo
--                JEFE_AREA (y ADMIN) lo tienen.
-- Desarrollado : Equipo SPSRT - UNIR
-- Fecha        : 2026-08-21
-- =====================================================================

DELETE FROM msa_rol_modulo rm
 USING msa_rol r, msa_modulo m
 WHERE rm.rol_id = r.id
   AND rm.modulo_id = m.id
   AND r.codigo = 'GESTOR_PROYECTO'
   AND m.codigo = 'DASHBOARD_JEFE';