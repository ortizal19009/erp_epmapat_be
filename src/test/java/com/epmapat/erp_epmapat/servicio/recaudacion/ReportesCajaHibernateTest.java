package com.epmapat.erp_epmapat.servicio.recaudacion;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.hibernate.engine.query.spi.ParameterParser;
import org.springframework.data.jpa.repository.Query;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.repositorio.RubroxfacR;

class ReportesCajaHibernateTest {
    @Test void hibernatePreservaSqlDeLasDoceConsultasDeCaja() {
        int count = 0;
        for (Class<?> repo : new Class<?>[]{FacturasR.class, RubroxfacR.class}) {
            for (java.lang.reflect.Method method : repo.getDeclaredMethods()) {
                Query query = method.getAnnotation(Query.class);
                if (query == null || !query.value().startsWith("WITH facturas_cobro AS")) continue;
                StringBuilder parsed = new StringBuilder();
                ParameterParser.parse(query.value(), new ParameterParser.Recognizer() {
                    public void outParameter(int p) { fail("Parametro de salida inesperado"); }
                    public void ordinalParameter(int p) { parsed.append('?'); }
                    public void namedParameter(String name, int p) { fail("Parametro inesperado: " + name); }
                    public void jpaPositionalParameter(int name, int p) { parsed.append('?'); }
                    public void other(char c) { parsed.append(c); }
                    public void complete() {}
                });
                assertEquals(query.value().replaceAll("\\?[0-9]+", "?"), parsed.toString(), method.getName());
                count++;
            }
        }
        assertEquals(12, count);
    }
}
