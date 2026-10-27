package com.epmapat.erp_epmapat.nc;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class NcEndosoAccessTest {
 private JdbcTemplate db; private NcEndosoService service;
 private MockHttpServletRequest request(long id){var r=new MockHttpServletRequest();r.setAttribute("jwtUserId",id);return r;}
 @BeforeEach void setup(){db=mock(JdbcTemplate.class);service=new NcEndosoService(db,new ObjectMapper(),true,BigDecimal.TEN,false);}
 private void active(long id){when(db.queryForObject("SELECT count(*) FROM usuarios WHERE idusuario=? AND estado=true",Integer.class,id)).thenReturn(1);when(db.queryForObject("SELECT to_regclass('public.nc_saldos') IS NOT NULL",Boolean.class)).thenReturn(true);}
 @Test void adminHasAllActionsWithoutExplicitGrants(){active(1L);var r=request(1);assertEquals(6,service.permisos(r).size());for(String p:List.of("CONSULTAR","SOLICITAR","AUTORIZAR","EJECUTAR","REVERTIR","REPORTES"))assertEquals(1,service.actor(r,p));}
 @Test void assignedUserGetsOnlyGrantedActions(){active(2L);when(db.queryForList(anyString(),eq(String.class),eq(2L))).thenReturn(List.of("CONSULTAR","SOLICITAR"));assertEquals(2,service.actor(request(2),"CONSULTAR"));assertEquals(2,service.actor(request(2),"SOLICITAR"));assertEquals(403,assertThrows(ResponseStatusException.class,()->service.actor(request(2),"AUTORIZAR")).getRawStatusCode());}
 @Test void unassignedUserIsRejected(){active(2L);when(db.queryForList(anyString(),eq(String.class),eq(2L))).thenReturn(List.of());assertEquals(403,assertThrows(ResponseStatusException.class,()->service.actor(request(2),"CONSULTAR")).getRawStatusCode());}
 @Test void inactiveAdminIsRejected(){when(db.queryForObject(anyString(),eq(Integer.class),eq(1L))).thenReturn(0);assertEquals(403,assertThrows(ResponseStatusException.class,()->service.actor(request(1),"CONSULTAR")).getRawStatusCode());}
 @Test void anonymousIsRejected(){assertEquals(401,assertThrows(ResponseStatusException.class,()->service.permisos(new MockHttpServletRequest())).getRawStatusCode());verifyNoInteractions(db);}
 @Test void explicitDisableStillAppliesToAdmin(){var disabled=new NcEndosoService(db,new ObjectMapper(),false,BigDecimal.TEN,false);assertEquals(503,assertThrows(ResponseStatusException.class,()->disabled.actor(request(1),"CONSULTAR")).getRawStatusCode());verifyNoInteractions(db);}
 @Test void missingSchemaGivesActionableError(){active(1L);when(db.queryForObject("SELECT to_regclass('public.nc_saldos') IS NOT NULL",Boolean.class)).thenReturn(false);var error=assertThrows(ResponseStatusException.class,()->service.permisos(request(1)));assertEquals(503,error.getRawStatusCode());assertTrue(error.getReason().contains("2026-10-19_nc_endosos.sql"));}
}
