package com.github.victormpcmun.c43spanishbankprocessor.printer;

import com.github.victormpcmun.c43spanishbankprocessor.parser.C43LineParser;
import com.github.victormpcmun.c43spanishbankprocessor.parser.RecordGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordLineFormatterTest {

    private static final String HEADER =
            "112100041802000513322609012609302000000001500009783VICTOR EJEMPLO SL            ";

    private final C43LineParser parser = new C43LineParser();
    private final RecordLineFormatter formatter = new RecordLineFormatter();

    @Test
    void writesTheWholeRecordAsNameValuePairs() {
        assertEquals("Registro:11 Cabecera de cuenta"
                + " - Clave de la entidad:2100"
                + " - Clave de oficina:0418"
                + " - Número de cuenta:0200051332"
                + " - Fecha inicial:260901 (2026-09-01)"
                + " - Fecha final:260930 (2026-09-30)"
                + " - Clave debe o haber del saldo inicial:2 (haber)"
                + " - Saldo inicial:00000000150000 (1500.00)"
                + " - Clave de divisa:978 (EUR)"
                + " - Modalidad de información:3"
                + " - Nombre abreviado:VICTOR EJEMPLO SL", format(HEADER));
    }

    @Test
    void writesAMovementAndItsComplementsOnTheSameLine() {
        RecordGroup movement = RecordGroup.of(parser.parse(1, "22    041826090326090312004100000000004599" + " ".repeat(38)))
                .with(parser.parse(2, "2301" + "COMPRA EN CAFETERIA".repeat(2) + " ".repeat(38)))
                .with(parser.parse(3, "240184000000000005000" + " ".repeat(59)));

        String line = formatter.format(movement);

        assertEquals(1, line.lines().count());
        assertTrue(line.startsWith("Registro:22 Movimiento - "));
        assertTrue(line.contains(" - Importe:00000000004599 (45.99) - Registro:23 Concepto complementario - "));
        assertTrue(line.contains(" - Registro:24 Equivalencia de divisa - "));
        assertTrue(line.endsWith(" - Importe en divisa origen:00000000005000 (50.00)"));
    }

    @Test
    void leavesBlankFieldsOut() {
        String movementWithoutReferences = "22    041826090326090312004100000000004599" + " ".repeat(38);

        String line = format(movementWithoutReferences);

        assertTrue(line.contains(" - Importe:00000000004599 (45.99)"));
        assertFalse(line.contains("Referencia"));
        assertFalse(line.contains("Número de documento"));
    }

    @Test
    void endsWithAWarningWhenTheLineHasTheWrongLength() {
        assertTrue(format(HEADER.substring(0, 70)).endsWith(" - Aviso:70 caracteres, 80 esperados"));
        assertFalse(format(HEADER).contains("Aviso"));
    }

    @Test
    void showsAnUnknownLineAsItIs() {
        assertEquals("Registro:desconocido - Contenido:XX not a C43 record", format("XX not a C43 record")
                .replace(" - Aviso:19 caracteres, 80 esperados", ""));
    }

    private String format(String line) {
        return formatter.format(RecordGroup.of(parser.parse(1, line)));
    }
}
