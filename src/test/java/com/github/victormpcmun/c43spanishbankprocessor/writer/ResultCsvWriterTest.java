package com.github.victormpcmun.c43spanishbankprocessor.writer;

import com.github.victormpcmun.c43spanishbankprocessor.C43Exception;
import com.github.victormpcmun.c43spanishbankprocessor.model.ResultRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultCsvWriterTest {

    @TempDir
    Path tempDir;

    private final ResultCsvWriter writer = new ResultCsvWriter();

    @Test
    void writesOneLinePerRowWithItsFiveColumns() throws IOException {
        Path file = tempDir.resolve("result.csv");

        writer.write(file, List.of(row("2026", "JULIO", "Ocio", "Restaurantes", "45.99"),
                row("2026", "SEPTIEMBRE", "Nomina", "Empresa", "2100.0")));

        assertEquals(List.of("2026;JULIO;Ocio;Restaurantes;45.99", "2026;SEPTIEMBRE;Nomina;Empresa;2100.0"),
                Files.readAllLines(file, TextFileWriter.CHARSET));
    }

    @Test
    void valuesWithSemicolonsOrQuotesAreQuoted() throws IOException {
        Path file = tempDir.resolve("result.csv");

        writer.write(file, List.of(row("2026", "JULIO", "Ocio;y cultura", "Dice \"hola\"", "45.99")));

        assertEquals(List.of("2026;JULIO;\"Ocio;y cultura\";\"Dice \"\"hola\"\"\";45.99"),
                Files.readAllLines(file, TextFileWriter.CHARSET));
    }

    @Test
    void accentsAreWrittenInWindows1252() throws IOException {
        Path file = tempDir.resolve("result.csv");

        writer.write(file, List.of(row("2026", "JULIO", "Nómina", "Señalización", "45.99")));

        assertEquals("2026;JULIO;Nómina;Señalización;45.99", Files.readString(file, TextFileWriter.CHARSET).strip());
    }

    @Test
    void missingDirectoriesAreCreatedAndAnEmptyResultGivesAnEmptyFile() throws IOException {
        Path file = tempDir.resolve("nested/result.csv");

        writer.write(file, List.of());

        assertTrue(Files.exists(file));
        assertEquals(List.of(), Files.readAllLines(file, TextFileWriter.CHARSET));
    }

    @Test
    void aFileThatCannotBeWrittenIsReported() {
        assertThrows(C43Exception.class, () -> writer.write(tempDir, List.of()));
    }

    private static ResultRow row(String year, String month, String category, String subcategory,
                                           String importe) {
        return new ResultRow(year, month, category, subcategory, importe);
    }
}
