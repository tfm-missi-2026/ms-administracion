package pe.unir.tfm.srp.administracion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.unir.tfm.srp.administracion.config.CurrentUserResolver;
import pe.unir.tfm.srp.administracion.dto.conversor.ModuloConversor;
import pe.unir.tfm.srp.administracion.dto.conversor.RolConversor;
import pe.unir.tfm.srp.administracion.dto.request.AsignarModulosRolRequest;
import pe.unir.tfm.srp.administracion.dto.request.RolCrearRequest;
import pe.unir.tfm.srp.administracion.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.administracion.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.administracion.model.Modulo;
import pe.unir.tfm.srp.administracion.model.Rol;
import pe.unir.tfm.srp.administracion.model.RolModulo;
import pe.unir.tfm.srp.administracion.repository.ModuloMapper;
import pe.unir.tfm.srp.administracion.repository.RolMapper;
import pe.unir.tfm.srp.administracion.repository.RolModuloMapper;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    private static final UUID ROL_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID LANDING_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
    private static final UUID SECCION_OPERACION = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID SECCION_SEGUIMIENTO = UUID.fromString("00000000-0000-0000-0000-000000000202");
    private static final UUID SECCION_ADMIN = UUID.fromString("00000000-0000-0000-0000-000000000203");
    private static final UUID SECCION_CUENTA = UUID.fromString("00000000-0000-0000-0000-000000000204");

    @Mock
    private RolMapper rolMapper;

    @Mock
    private ModuloMapper moduloMapper;

    @Mock
    private RolModuloMapper rolModuloMapper;

    @Mock
    private RolConversor rolConversor;

    @Mock
    private ModuloConversor moduloConversor;

    @Mock
    private CurrentUserResolver currentUserResolver;

    @InjectMocks
    private RolService rolService;

    private Modulo seccion(UUID id, String codigo) {
        return Modulo.builder()
                .id(id)
                .codigo(codigo)
                .nombre(codigo)
                .tipo("SECTION")
                .estado((short) 1)
                .orden((short) 1)
                .build();
    }

    private List<Modulo> secciones() {
        return List.of(
                seccion(SECCION_OPERACION, "OPERACION"),
                seccion(SECCION_SEGUIMIENTO, "SEGUIMIENTO"),
                seccion(SECCION_ADMIN, "SECCION_ADMINISTRACION"),
                seccion(SECCION_CUENTA, "CUENTA")
        );
    }

    private Modulo landing() {
        return Modulo.builder()
                .id(LANDING_ID)
                .codigo("INICIO")
                .nombre("Inicio")
                .tipo("MENU")
                .estado((short) 1)
                .build();
    }

    @Test
    void crear_otorgaLandingYTodasLasSecciones() {
        when(rolMapper.findByCode("QA")).thenReturn(null);
        when(moduloMapper.findById(LANDING_ID)).thenReturn(landing());
        when(moduloMapper.listActiveSections()).thenReturn(secciones());
        when(rolMapper.findById(any())).thenReturn(null);

        rolService.create(new RolCrearRequest("QA", "QA Rol", "desc", LANDING_ID));

        ArgumentCaptor<RolModulo> captor = ArgumentCaptor.forClass(RolModulo.class);
        verify(rolModuloMapper, org.mockito.Mockito.atLeastOnce()).insert(captor.capture());

        List<UUID> insertados = captor.getAllValues().stream()
                .map(RolModulo::getModuloId)
                .toList();
        assertThat(insertados).contains(LANDING_ID);
        assertThat(insertados).contains(SECCION_OPERACION, SECCION_SEGUIMIENTO,
                SECCION_ADMIN, SECCION_CUENTA);
    }

    @Test
    void crear_codigoSistemaExistente_lanzaConflicto() {
        Rol sistema = Rol.builder().id(UUID.randomUUID()).codigo("ADMIN").sistema(true).build();
        when(rolMapper.findByCode("ADMIN")).thenReturn(sistema);

        assertThatThrownBy(() -> rolService.create(
                new RolCrearRequest("ADMIN", "Otro", "desc", LANDING_ID)))
                .isInstanceOf(ConflictoNegocioException.class);

        verify(rolMapper, never()).insert(any());
        verify(rolModuloMapper, never()).insert(any());
    }

    @Test
    void crear_codigoDuplicadoNoSistema_lanzaConflicto() {
        Rol existente = Rol.builder().id(UUID.randomUUID()).codigo("QA").sistema(false).build();
        when(rolMapper.findByCode("QA")).thenReturn(existente);

        assertThatThrownBy(() -> rolService.create(
                new RolCrearRequest("QA", "Otro", "desc", LANDING_ID)))
                .isInstanceOf(ConflictoNegocioException.class);

        verify(rolMapper, never()).insert(any());
    }

    @Test
    void replaceModules_conModulosIncluyendoLandingYSeccionesPersistidas_reotorgaLasQueFaltan() {
        Rol rol = Rol.builder()
                .id(ROL_ID)
                .codigo("QA")
                .nombre("QA Rol")
                .paginaInicioId(LANDING_ID)
                .sistema(false)
                .build();
        when(rolMapper.findById(ROL_ID)).thenReturn(rol);

        UUID moduloMenu = UUID.fromString("00000000-0000-0000-0000-000000000102");
        when(moduloMapper.findById(moduloMenu)).thenReturn(
                Modulo.builder().id(moduloMenu).codigo("PROYECTOS").tipo("MENU").estado((short) 1).build());
        when(moduloMapper.findById(LANDING_ID)).thenReturn(landing());

        when(rolModuloMapper.count(ROL_ID, SECCION_OPERACION)).thenReturn(0);
        when(rolModuloMapper.count(ROL_ID, SECCION_SEGUIMIENTO)).thenReturn(1);
        when(rolModuloMapper.count(ROL_ID, SECCION_ADMIN)).thenReturn(0);
        when(rolModuloMapper.count(ROL_ID, SECCION_CUENTA)).thenReturn(0);

        when(moduloMapper.listActiveSections()).thenReturn(secciones());
        when(moduloMapper.listByRole(ROL_ID)).thenReturn(List.of());

        rolService.replaceModules(ROL_ID,
                new AsignarModulosRolRequest(List.of(LANDING_ID, moduloMenu)));

        ArgumentCaptor<RolModulo> captor = ArgumentCaptor.forClass(RolModulo.class);
        verify(rolModuloMapper, org.mockito.Mockito.atLeastOnce()).insert(captor.capture());

        List<UUID> insertados = captor.getAllValues().stream()
                .map(RolModulo::getModuloId)
                .toList();
        assertThat(insertados).contains(moduloMenu, LANDING_ID, SECCION_OPERACION, SECCION_ADMIN, SECCION_CUENTA);
        assertThat(insertados).doesNotContain(SECCION_SEGUIMIENTO);
    }

    @Test
    void replaceModules_sinLanding_lanzaConflicto() {
        Rol rol = Rol.builder()
                .id(ROL_ID)
                .codigo("QA")
                .nombre("QA Rol")
                .paginaInicioId(LANDING_ID)
                .build();
        when(rolMapper.findById(ROL_ID)).thenReturn(rol);

        UUID otroModulo = UUID.fromString("00000000-0000-0000-0000-000000000103");
        assertThatThrownBy(() -> rolService.replaceModules(ROL_ID,
                new AsignarModulosRolRequest(List.of(otroModulo))))
                .isInstanceOf(ConflictoNegocioException.class);

        verify(rolModuloMapper, never()).deleteByRole(any());
        verify(rolModuloMapper, never()).insert(any());
    }

    @Test
    void replaceModules_rolNoExiste_lanzaRecursoNoEncontrado() {
        when(rolMapper.findById(ROL_ID)).thenReturn(null);

        assertThatThrownBy(() -> rolService.replaceModules(ROL_ID,
                new AsignarModulosRolRequest(List.of(LANDING_ID))))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(rolModuloMapper, never()).deleteByRole(any());
        verify(rolModuloMapper, never()).insert(any());
    }
}
