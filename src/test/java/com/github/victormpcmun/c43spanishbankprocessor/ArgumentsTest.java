package com.github.victormpcmun.c43spanishbankprocessor;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgumentsTest {

    @Test
    void readsTheFourPaths() {
        Arguments arguments = Arguments.parse(new String[]{"a.c43", "b.csv", "c.csv", "d.log"});

        assertEquals(Path.of("a.c43"), arguments.c43File());
        assertEquals(Path.of("b.csv"), arguments.resultPath());
        assertEquals(Path.of("c.csv"), arguments.definitionPath());
        assertEquals(Path.of("d.log"), arguments.logPath());
    }

    @Test
    void everyArgumentIsMandatory() {
        assertThrows(C43Exception.class, () -> Arguments.parse(new String[]{"a.c43"}));
        assertThrows(C43Exception.class, () -> Arguments.parse(new String[]{"a.c43", "b.csv", "c.csv"}));
        assertThrows(C43Exception.class, () -> Arguments.parse(new String[]{"a", "b", "c", "d", "e"}));
    }
}
