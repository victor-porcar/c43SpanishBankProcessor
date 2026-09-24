package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.C43Exception;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class C43FilesTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void createFiles() throws IOException {
        Files.createFile(tempDir.resolve("enero.txt"));
        Files.createFile(tempDir.resolve("febrero.txt"));
        Files.createFile(tempDir.resolve("marzo.c43"));
        Files.createFile(tempDir.resolve("notes.md"));
        Files.createDirectory(tempDir.resolve("old.txt"));
    }

    @Test
    void aPathWithoutWildcardsIsTheFileItself() {
        assertEquals(List.of(Path.of("D:/C43/enero.txt")), C43Files.matching("D:/C43/enero.txt"));
    }

    @Test
    void asteriskTakesEveryFileOfThatKind() {
        assertEquals(List.of(tempDir.resolve("enero.txt"), tempDir.resolve("febrero.txt")),
                C43Files.matching(tempDir + "/*.txt"));
    }

    @Test
    void asteriskDotAsteriskTakesEveryFile() {
        assertEquals(List.of(tempDir.resolve("enero.txt"), tempDir.resolve("febrero.txt"),
                        tempDir.resolve("marzo.c43"), tempDir.resolve("notes.md")),
                C43Files.matching(tempDir + "/*.*"));
    }

    @Test
    void questionMarkStandsForOneCharacter() {
        assertEquals(List.of(tempDir.resolve("marzo.c43")), C43Files.matching(tempDir + "/marz?.c43"));
    }

    @Test
    void filesAreTakenInTheOrderOfTheirNames() {
        List<String> names = C43Files.matching(tempDir + "/*.*").stream()
                .map(file -> file.getFileName().toString()).toList();

        assertEquals(names.stream().sorted().toList(), names);
    }

    @Test
    void directoriesAreLeftOutEvenWhenTheyMatch() {
        assertTrue(C43Files.matching(tempDir + "/*.txt").stream().noneMatch(file -> file.endsWith("old.txt")));
    }

    @Test
    void aPatternMatchingNothingIsReported() {
        C43Exception error = assertThrows(C43Exception.class, () -> C43Files.matching(tempDir + "/*.xml"));

        assertTrue(error.getMessage().contains("no C43 file matches"));
    }

    @Test
    void anEmptyPatternIsReported() {
        assertThrows(C43Exception.class, () -> C43Files.matching(""));
        assertThrows(C43Exception.class, () -> C43Files.matching("   "));
        assertThrows(C43Exception.class, () -> C43Files.matching(null));
    }
}
