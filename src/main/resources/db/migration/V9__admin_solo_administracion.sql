-- =====================================================================
-- Sistema      : SPSRT - Sistema de Planificacion y Seguimiento de Recursos Tecnicos
-- Modulo       : MS Administracion
-- Objetivo     : El rol ADMIN solo ve los modulos de administracion
--                (usuarios, roles, catalogo, modulos) y configuracion;
--                no participa en la planificacion ni en el seguimiento.
--                Su pagina de inicio pasa a USUARIOS.
-- Desarrollado : Equipo SPSRT - UNIR
-- Fecha        : 2026-11-03
-- =====================================================================

UPDATE msa_rol
   SET pagina_inicio_id = (SELECT id FROM msa_modulo WHERE codigo = 'USUARIOS')
 WHERE codigo = 'ADMIN';

DELETE FROM msa_rol_modulo rm
 USING msa_rol r, msa_modulo m
 WHERE rm.rol_id = r.id
   AND rm.modulo_id = m.id
   AND r.codigo = 'ADMIN'
   AND m.codigo NOT IN ('USUARIOS', 'ROLES', 'CATALOGO', 'MODULOS', 'CONFIGURACION',
                        'SECCION_ADMINISTRACION', 'CUENTA');
