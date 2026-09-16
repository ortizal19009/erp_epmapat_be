package com.epmapat.erp_epmapat.servicio;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import com.epmapat.erp_epmapat.repositorio.ClientesR;
class ClienteBusquedaTest {
    ClienteServicio servicio = new ClienteServicio();
    ClientesR repo = mock(ClientesR.class);
    @BeforeEach void setup() { ReflectionTestUtils.setField(servicio,"dao",repo); }
    @Test void noConsultaTerminosVaciosCortosOExcesivos() {
        for (String termino : new String[]{null,"", "   "," a ","ab","x".repeat(101)}) {
            assertTrue(servicio.findByNombreIdentifi(termino).isEmpty());
        }
        verifyNoInteractions(repo);
    }
    @Test void limitaResultadosYNormalizaTermino() {
        servicio.findByNombreIdentifi("  MARIA  ");
        verify(repo).findByNombreIdentifi("%maria%",PageRequest.of(0,50));
    }
    @Test void comodinesSonTextoLiteral() {
        servicio.findByNombreIdentifi("ABC%_!");
        verify(repo).findByNombreIdentifi("%abc!%!_!!%",PageRequest.of(0,50));
    }
}
