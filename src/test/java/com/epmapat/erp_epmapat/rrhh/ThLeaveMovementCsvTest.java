package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.epmapat.erp_epmapat.rrhh.dto.*;

class ThLeaveMovementCsvTest {
    @Test void preservesSignedAmountsOriginAndEscapesFormulaText() {
        var row = new ThLeaveMovementResponse(5L, 3L, 2026, 7L, 4L, "REINTEGRO", new BigDecimal("2.50"),
                new BigDecimal("1.00"), new BigDecimal("3.50"), new BigDecimal("2.50"), BigDecimal.ZERO,
                new BigDecimal("3.50"), 8L, null, " =SUM(1);\"x\"\ntexto");
        String csv = new String(ThLeaveMovementCsv.render(9L, List.of(row)), StandardCharsets.UTF_8);
        assertTrue(csv.startsWith("\uFEFFPersonal;"));
        assertTrue(csv.contains("9;5;3;2026;7;4;\"REINTEGRO\";2.50;1.00;3.50;2.50;0;3.50;8;"));
        assertTrue(csv.contains("\"' =SUM(1);\"\"x\"\"\ntexto\""));
        var negative = new ThLeaveMovementResponse(4L, 3L, 2026, 7L, null, "CONSUMO", new BigDecimal("-2.50"),
                null, null, null, null, null, 8L, null, null);
        assertTrue(new String(ThLeaveMovementCsv.render(9L, List.of(negative)), StandardCharsets.UTF_8).contains(";-2.50;"));
    }

    @Test void emptyBookContainsOnlyHeader() {
        String csv = new String(ThLeaveMovementCsv.render(9L, List.of()), StandardCharsets.UTF_8);
        assertEquals(1, csv.lines().count());
    }
}
