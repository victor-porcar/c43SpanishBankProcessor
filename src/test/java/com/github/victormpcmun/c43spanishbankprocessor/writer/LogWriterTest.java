package com.github.victormpcmun.c43spanishbankprocessor.writer;

import com.github.victormpcmun.c43spanishbankprocessor.model.MovementLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogWriterTest {

    @TempDir
    Path tempDir;

    private final LogWriter writer = new LogWriter();

    @Test
    void writesOneLinePerMovementWithoutCategory() throws IOException {
        Path file = tempDir.resolve("process.log");

        writer.write(file, List.of(new MovementLine("2026", "JULIO", "45.99", "Registro:22 Movimiento - Importe:x")));

        List<String> lines = Files.readAllLines(file, TextFileWriter.CHARSET);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).startsWith("NO CATEGORY MATCHES IT, the default one was used:"));
        assertTrue(lines.get(0).contains("year=2026"));
        assertTrue(lines.get(0).contains("month=JULIO"));
        assertTrue(lines.get(0).contains("importe=45.99"));
        assertTrue(lines.get(0).endsWith("line=Registro:22 Movimiento - Importe:x"));
    }

    @Test
    void theFileIsWrittenEvenWhenEveryMovementFoundItsCategory() throws IOException {
        Path file = tempDir.resolve("nested/process.log");

        writer.write(file, List.of());

        assertTrue(Files.exists(file));
        assertEquals(List.of(), Files.readAllLines(file, TextFileWriter.CHARSET));
    }
}
