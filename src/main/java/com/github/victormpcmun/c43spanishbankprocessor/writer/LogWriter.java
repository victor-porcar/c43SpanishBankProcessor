package com.github.victormpcmun.c43spanishbankprocessor.writer;

import com.github.victormpcmun.c43spanishbankprocessor.model.MovementLine;

import java.nio.file.Path;
import java.util.List;

/**
 * Writes the log file: one line per movement that matched no category and went to the result
 * with the default one. The file is written on every run, empty when every movement found its
 * own category.
 */
public class LogWriter {

    public void write(Path file, List<MovementLine> movementsWithoutCategory) {
        TextFileWriter.write(file, movementsWithoutCategory.stream().map(LogWriter::lineOf).toList(), "log file");
    }

    private static String lineOf(MovementLine movement) {
        return "NO CATEGORY MATCHES IT, the default one was used: year=%s month=%s importe=%s line=%s"
                .formatted(movement.getYear(), movement.getMonth(), movement.getImporte(), movement.getPlainLine());
    }
}
