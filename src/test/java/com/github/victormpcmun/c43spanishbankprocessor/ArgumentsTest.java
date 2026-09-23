package com.github.victormpcmun.c43spanishbankprocessor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgumentsTest {

    @Test
    void readsTheFourPathsAndTheDefaultCategory() {
        Arguments arguments = parse("a.c43", "b.csv", "c.csv", "d.log", "SIN CLASIFICAR;REVISAR");

        assertEquals(Path.of("a.c43"), arguments.c43File());
        assertEquals(Path.of("b.csv"), arguments.resultPath());
        assertEquals(Path.of("c.csv"), arguments.definitionPath());
        assertEquals(Path.of("d.log"), arguments.logPath());
        assertEquals("SIN CLASIFICAR", arguments.defaultCategory().getCategory());
        assertEquals("REVISAR", arguments.defaultCategory().getSubcategory());
    }

    @Test
    void theDefaultCategoryMatchesNoMovementByItself() {
        Arguments arguments = parse("a.c43", "b.csv", "c.csv", "d.log", "SIN CLASIFICAR;REVISAR");

        assertFalse(new com.github.victormpcmun.c43spanishbankprocessor.model.MovementLine(
                "2026", "JULIO", "1.0", "any line").matchPattern(arguments.defaultCategory().getDefinition()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"SIN CLASIFICAR", "", ";REVISAR", "SIN CLASIFICAR;", "A;B;C", "   ;   "})
    void aWronglyWrittenDefaultCategoryIsRejected(String defaultCategory) {
        assertThrows(C43Exception.class, () -> parse("a.c43", "b.csv", "c.csv", "d.log", defaultCategory));
    }

    @Test
    void everyArgumentIsMandatory() {
        assertThrows(C43Exception.class, () -> Arguments.parse(new String[]{"a.c43"}));
        assertThrows(C43Exception.class, () -> Arguments.parse(new String[]{"a.c43", "b.csv", "c.csv", "d.log"}));
        assertThrows(C43Exception.class, () -> Arguments.parse(new String[]{"a", "b", "c", "d", "E;F", "extra"}));
    }

    private static Arguments parse(String... args) {
        return Arguments.parse(args);
    }
}
