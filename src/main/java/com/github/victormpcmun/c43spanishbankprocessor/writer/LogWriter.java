package com.github.victormpcmun.c43spanishbankprocessor.writer;

import com.github.victormpcmun.c43spanishbankprocessor.model.MovementLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes the log file: one line per movement left out for being a repeat of another and one per
 * movement that matched no category and took the default one. The file is written on every run,
 * empty when there is nothing to tell.
 */
public class LogWriter {

    private static final String DUPLICATE = "DUPLICATED MOVEMENT, left out of the result: ";
    private static final String WITHOUT_CATEGORY = "NO CATEGORY MATCHES IT, the default one was used: ";

    public void write(Path file, List<MovementLine> duplicates, List<MovementLine> movementsWithoutCategory) {
        List<String> lines = new ArrayList<>();
        duplicates.forEach(movement -> lines.add(lineOf(DUPLICATE, movement)));
        movementsWithoutCategory.forEach(movement -> lines.add(lineOf(WITHOUT_CATEGORY, movement)));
        TextFileWriter.write(file, lines, "log file");
    }

    /** Writes what went wrong, with the whole stack trace, so the console can stay quiet. */
    public void writeError(Path file, Throwable error) {
        TextFileWriter.write(file, List.of(stackTraceOf(error)), "log file");
    }

    private static String lineOf(String reason, MovementLine movement) {
        return reason + "year=%s month=%s importe=%s line=%s".formatted(
                movement.getYear(), movement.getMonth(), movement.getImporte(), movement.getPlainLine());
    }

    private static String stackTraceOf(Throwable error) {
        StringWriter text = new StringWriter();
        error.printStackTrace(new PrintWriter(text));
        return text.toString();
    }
}
