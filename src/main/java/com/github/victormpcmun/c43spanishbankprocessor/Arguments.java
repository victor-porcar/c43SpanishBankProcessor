package com.github.victormpcmun.c43spanishbankprocessor;

import com.github.victormpcmun.c43spanishbankprocessor.model.DefinitionForCategorySubcategory;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * Command line arguments: the C43 file to read, the result file, the CSV of categories, the log
 * file and the category given to the movements that match no definition.
 */
public record Arguments(Path c43File, Path resultPath, Path definitionPath, Path logPath,
                        DefinitionForCategorySubcategory defaultCategory) {

    private static final int EXPECTED_ARGUMENTS = 5;
    private static final String DEFAULT_CATEGORY_SEPARATOR = ";";
    private static final int DEFAULT_CATEGORY_PARTS = 2;

    private static final String USAGE = """
            expected 5 arguments
              1. C43 file to read
              2. Result csv file
              3. CSV file with the categories: category;subcategory;definition
              4. Log file, where the movements that match no definition are written
              5. Category of the movements that match no definition, as CATEGORY;SUBCATEGORY
            example:
              java -jar c43SpanishBankProcessor.jar "D:\\bank\\statement.c43" "D:\\bank\\result.csv" \
            "D:\\bank\\categories.csv" "D:\\bank\\process.log" "SIN CLASIFICAR;REVISAR\"""";

    public static Arguments parse(String[] args) {
        if (args.length != EXPECTED_ARGUMENTS) {
            throw new C43Exception(USAGE);
        }
        return new Arguments(pathOf(args[0]), pathOf(args[1]), pathOf(args[2]), pathOf(args[3]),
                defaultCategoryOf(args[4]));
    }

    /** "CATEGORY;SUBCATEGORY", with a definition of its own that matches nothing. */
    private static DefinitionForCategorySubcategory defaultCategoryOf(String value) {
        String[] parts = value.split(DEFAULT_CATEGORY_SEPARATOR, -1);
        if (parts.length != DEFAULT_CATEGORY_PARTS || parts[0].isBlank() || parts[1].isBlank()) {
            throw new C43Exception("default category must be written as CATEGORY;SUBCATEGORY: '" + value + "'");
        }
        return new DefinitionForCategorySubcategory(parts[0].strip(), parts[1].strip(), "");
    }

    private static Path pathOf(String value) {
        try {
            return Path.of(value);
        } catch (InvalidPathException e) {
            throw new C43Exception("invalid path: '" + value + "'", e);
        }
    }
}
