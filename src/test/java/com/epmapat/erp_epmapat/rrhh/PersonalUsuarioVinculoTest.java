package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import com.epmapat.erp_epmapat.rrhh.modelo.Personal;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import com.epmapat.erp_epmapat.rrhh.servicio.PersonalServicio;
import com.epmapat.erp_epmapat.repositorio.administracion.UsuariosR;
import com.fasterxml.jackson.databind.ObjectMapper;

class PersonalUsuarioVinculoTest {
    PersonalR dao = mock(PersonalR.class);
    UsuariosR users = mock(UsuariosR.class);
    PersonalServicio service = new PersonalServicio(dao,mock(ContemergenciasR.class),mock(CargosR.class),mock(TpcontratosR.class),users);

    Personal personal(long id) { Personal p = new Personal(); p.setIdpersonal(id); return p; }
    UsuariosR.PersonalVinculo link(long personal, long user) {
        return new UsuariosR.PersonalVinculo() {
            public Long getIdpersonal() { return personal; }
            public Long getIdusuario() { return user; }
            public String getUsuario() { return "usuario"; }
        };
    }
    @Test void resolvesListInOneQueryAndDoesNotInferByIdentity() {
        var a = personal(7); var b = personal(8); b.setIdentificacion("1234567890");
        when(dao.findAll()).thenReturn(List.of(a,b));
        when(users.findPersonalVinculos(anyList())).thenReturn(List.of(link(7,11)));
        service.findAll();
        assertTrue(a.getTieneUsuario()); assertEquals(11L,a.getIdusuarioVinculado()); assertEquals("usuario",a.getUsuarioSistema());
        assertFalse(b.getTieneUsuario()); assertNull(b.getIdusuarioVinculado());
        verify(users,times(1)).findPersonalVinculos(argThat(ids -> ids.size()==2 && ids.containsAll(List.of(7L,8L))));
    }
    @Test void detailUsesRealLinkAndKeepsDeterministicFirstUser() {
        var p = personal(7); when(dao.findById(7L)).thenReturn(Optional.of(p));
        when(users.findPersonalVinculos(anyList())).thenReturn(List.of(link(7,11),link(7,12)));
        assertEquals(11L,service.findById(7L).getIdusuarioVinculado());
    }
    @Test void searchAlsoIncludesLinkAndEmptyListDoesNotQueryUsers() {
        when(dao.search(anyString(),isNull(),any())).thenReturn(new PageImpl<>(List.of(personal(7))));
        when(users.findPersonalVinculos(anyList())).thenReturn(List.of());
        assertFalse(service.search("",null,0,20).getContent().get(0).getTieneUsuario());
        clearInvocations(users); when(dao.findAll()).thenReturn(List.of()); service.findAll(); verifyNoInteractions(users);
    }
    @Test void clientCannotSupplyDerivedAccountState() throws Exception {
        var p = new ObjectMapper().readValue("{\"idpersonal\":7,\"tieneUsuario\":true,\"idusuarioVinculado\":1,\"usuarioSistema\":\"admin\"}",Personal.class);
        assertNull(p.getTieneUsuario()); assertNull(p.getIdusuarioVinculado()); assertNull(p.getUsuarioSistema());
    }
}
