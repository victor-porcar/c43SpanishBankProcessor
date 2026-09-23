package com.github.victormpcmun.c43spanishbankprocessor.model;

import com.github.victormpcmun.c43spanishbankprocessor.C43Exception;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Reads the CSV of categories: three columns separated by ';' holding category, subcategory and
 * definition. A line without the three values is ignored, and so is a blank one.
 */
public class DefinitionCsvReader {

    /** The charset Excel writes CSV files with in Spanish Windows. */
    private static final Charset CSV_CHARSET = Charset.forName("windows-1252");

    private static final int CATEGORY = 0;
    private static final int SUBCATEGORY = 1;
    private static final int DEFINITION = 2;
    private static final int COLUMNS = 3;

    public List<DefinitionForCategorySubcategory> read(Path file) {
        return linesOf(file).stream()
                .map(DefinitionCsvReader::definitionOf)
                .flatMap(Optional::stream)
                .toList();
    }

    private static Optional<DefinitionForCategorySubcategory> definitionOf(String line) {
        List<String> values = CsvLineSplitter.split(line);
        if (values.size() < COLUMNS || hasBlankValue(values)) {
            return Optional.empty();
        }
        return Optional.of(new DefinitionForCategorySubcategory(
                values.get(CATEGORY).strip(),
                values.get(SUBCATEGORY).strip(),
                values.get(DEFINITION).strip()));
    }

    private static boolean hasBlankValue(List<String> values) {
        return values.subList(CATEGORY, COLUMNS).stream().anyMatch(String::isBlank);
    }

    private static List<String> linesOf(Path file) {
        try {
            return Files.readAllLines(file, CSV_CHARSET);
        } catch (IOException e) {
            throw new C43Exception("cannot read definitions file " + file.toAbsolutePath(), e);
        }
    }
}
