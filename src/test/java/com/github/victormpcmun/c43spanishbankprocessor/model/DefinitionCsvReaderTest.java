package com.github.victormpcmun.c43spanishbankprocessor.model;

import com.github.victormpcmun.c43spanishbankprocessor.C43Exception;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefinitionCsvReaderTest {

    private final DefinitionCsvReader reader = new DefinitionCsvReader();

    @Test
    void readsOneDefinitionPerCompleteLine() {
        List<DefinitionForCategorySubcategory> definitions = reader.read(csv());

        assertEquals(List.of("Alimentacion", "Ocio;y cultura", "Nomina", "Comillas", "Vivienda", "SIN CLASIFICAR"),
                definitions.stream().map(DefinitionForCategorySubcategory::getCategory).toList());
    }

    @Test
    void keepsEveryColumnOfALine() {
        DefinitionForCategorySubcategory first = reader.read(csv()).get(0);

        assertEquals("Alimentacion", first.getCategory());
        assertEquals("Supermercado", first.getSubcategory());
        assertEquals("*MERCADONA*|*CARREFOUR*", first.getDefinition());
    }

    @Test
    void quotedValuesKeepTheirSemicolonsAndQuotes() {
        List<DefinitionForCategorySubcategory> definitions = reader.read(csv());

        assertEquals("Ocio;y cultura", definitions.get(1).getCategory());
        assertEquals("Dice \"hola\"", definitions.get(3).getSubcategory());
    }

    @Test
    void accentsAreReadRight() {
        assertEquals("*Concepto 1:NÓMINA*", reader.read(csv()).get(2).getDefinition());
    }

    @Test
    void extraColumnsAreIgnored() {
        DefinitionForCategorySubcategory last = reader.read(csv()).get(4);

        assertEquals("Vivienda", last.getCategory());
        assertEquals("*ALQUILER*", last.getDefinition());
    }

    @Test
    void aMissingFileIsReported() {
        assertThrows(C43Exception.class, () -> reader.read(Path.of("does-not-exist.csv")));
    }

    private static Path csv() {
        try {
            return Path.of(DefinitionCsvReaderTest.class.getClassLoader().getResource("definitions.csv").toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
