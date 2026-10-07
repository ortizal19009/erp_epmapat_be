package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.epmapat.erp_epmapat.rrhh.dto.*;
import com.epmapat.erp_epmapat.rrhh.modelo.*;

class ThLeaveInboxCsvTest {
    @Test void exportsOnlyProjectedPageWithEscapedTextAndNumericDays() {
        Personal person = new Personal(); person.setIdpersonal(7L);
        person.setNombres(" =HYPERLINK(\"x\")"); person.setApellidos("Pérez; \"Ana\"");
        ThLeaveRequest request = new ThLeaveRequest(); request.setIdrequest(8L);
        request.setIdpersonal_personal(person); request.setMotivo("@SUM(1)\nsegunda línea");
        request.setDias_solicitados(new BigDecimal("2.50"));
        String csv = new String(ThLeaveInboxCsv.render(new ThLeaveInboxResponse(
                List.of(ThLeaveInboxResponse.Solicitud.from(request)), 2, 10, 23, 3)), StandardCharsets.UTF_8);
        assertTrue(csv.startsWith("\uFEFFPágina;"));
        assertTrue(csv.contains("3;10;23;8;7;"));
        assertTrue(csv.contains("\"' =HYPERLINK(\"\"x\"\")\""));
        assertTrue(csv.contains("\"Pérez; \"\"Ana\"\"\""));
        assertTrue(csv.contains(";2.50;\"'@SUM(1)\nsegunda línea\""));
    }

    @Test void emptyPageExportsHeaderOnly() {
        String csv = new String(ThLeaveInboxCsv.render(new ThLeaveInboxResponse(List.of(), 0, 20, 0, 0)), StandardCharsets.UTF_8);
        assertEquals(1, csv.lines().count());
    }
}
