package com.github.victormpcmun.c43spanishbankprocessor.writer;

import com.github.victormpcmun.c43spanishbankprocessor.model.ResultRow;

import java.nio.file.Path;
import java.util.List;

/**
 * Writes the result file: one CSV line per row, with year, month, category, subcategory and
 * amount, separated by ';'.
 */
public class ResultCsvWriter {

    public void write(Path file, List<ResultRow> rows) {
        TextFileWriter.write(file, rows.stream().map(ResultCsvWriter::lineOf).toList(), "result file");
    }

    private static String lineOf(ResultRow row) {
        return CsvLineJoiner.join(row.year(), row.month(), row.category(), row.subcategory(), row.importe());
    }
}
