package com.github.victormpcmun.c43spanishbankprocessor;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgumentsTest {

    @Test
    void readsTheC43FileAndTheThreeOtherPaths() {
        Arguments arguments = Arguments.parse(new String[]{"a.c43", "b.csv", "c.csv", "d.log"});

        assertEquals(List.of("a.c43"), arguments.c43Files());
        assertEquals(Path.of("b.csv"), arguments.resultPath());
        assertEquals(Path.of("c.csv"), arguments.definitionPath());
        assertEquals(Path.of("d.log"), arguments.logPath());
    }

    @Test
    void theC43ArgumentMayHoldWildcards() {
        Arguments arguments = Arguments.parse(new String[]{"D:\\C43\\*.txt", "b.csv", "c.csv", "d.log"});

        assertEquals(List.of("D:\\C43\\*.txt"), arguments.c43Files());
    }

    @Test
    void everythingBeforeTheLastThreePathsIsAC43File() {
        Arguments arguments = Arguments.parse(
                new String[]{"enero.txt", "febrero.txt", "marzo.txt", "b.csv", "c.csv", "d.log"});

        assertEquals(List.of("enero.txt", "febrero.txt", "marzo.txt"), arguments.c43Files());
        assertEquals(Path.of("b.csv"), arguments.resultPath());
        assertEquals(Path.of("d.log"), arguments.logPath());
    }

    @Test
    void theFourArgumentsAreMandatory() {
        assertThrows(C43Exception.class, () -> Arguments.parse(new String[]{"a.c43"}));
        assertThrows(C43Exception.class, () -> Arguments.parse(new String[]{"a.c43", "b.csv", "c.csv"}));
        assertThrows(C43Exception.class, () -> Arguments.parse(new String[]{}));
    }
}
