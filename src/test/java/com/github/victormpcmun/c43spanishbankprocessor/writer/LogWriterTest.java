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

        writer.write(file, List.of(), List.of(new MovementLine("2026", "JULIO", "45.99", "Registro:22 Movimiento - Importe:x")));

        List<String> lines = Files.readAllLines(file, TextFileWriter.CHARSET);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).startsWith("NO CATEGORY MATCHES IT, the default one was used:"));
        assertTrue(lines.get(0).contains("year=2026"));
        assertTrue(lines.get(0).contains("month=JULIO"));
        assertTrue(lines.get(0).contains("importe=45.99"));
        assertTrue(lines.get(0).endsWith("line=Registro:22 Movimiento - Importe:x"));
    }

    @Test
    void writesTheDuplicatesFirstAndThenTheOnesWithoutCategory() throws IOException {
        Path file = tempDir.resolve("process.log");

        writer.write(file,
                List.of(new MovementLine("2026", "JULIO", "-10.0", "repeated movement")),
                List.of(new MovementLine("2026", "JULIO", "-20.0", "movement with no category")));

        List<String> lines = Files.readAllLines(file, TextFileWriter.CHARSET);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).startsWith("DUPLICATED MOVEMENT, left out of the result:"));
        assertTrue(lines.get(0).endsWith("line=repeated movement"));
        assertTrue(lines.get(1).startsWith("NO CATEGORY MATCHES IT, the default one was used:"));
    }

    @Test
    void anErrorIsWrittenWithItsWholeStackTrace() throws IOException {
        Path file = tempDir.resolve("process.log");

        writer.writeError(file, new IllegalStateException("something went wrong"));

        String log = Files.readString(file, TextFileWriter.CHARSET);
        assertTrue(log.startsWith("java.lang.IllegalStateException: something went wrong"));
        assertTrue(log.contains("at com.github.victormpcmun.c43spanishbankprocessor.writer.LogWriterTest"));
    }

    @Test
    void theFileIsWrittenEvenWhenEveryMovementFoundItsCategory() throws IOException {
        Path file = tempDir.resolve("nested/process.log");

        writer.write(file, List.of(), List.of());

        assertTrue(Files.exists(file));
        assertEquals(List.of(), Files.readAllLines(file, TextFileWriter.CHARSET));
    }
}
