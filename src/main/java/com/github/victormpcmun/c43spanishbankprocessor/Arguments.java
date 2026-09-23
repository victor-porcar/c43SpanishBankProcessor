package com.github.victormpcmun.c43spanishbankprocessor;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * Command line arguments: the C43 file to read, the result file, the CSV of categories and the
 * log file.
 */
public record Arguments(Path c43File, Path resultPath, Path definitionPath, Path logPath) {

    private static final int EXPECTED_ARGUMENTS = 4;

    private static final String USAGE = """
            expected 4 arguments
              1. C43 file to read
              2. Result csv file
              3. CSV file with the categories: category;subcategory;definition
                 (the line whose definition is *** gives the category of the movements that match no other)
              4. Log file, where the movements that match no category are written
            example:
              java -jar dist/c43SpanishBankProcessor.jar "D:\\bank\\statement.c43" "D:\\bank\\result.csv" \
            "D:\\bank\\categories.csv" "D:\\bank\\process.log\"""";

    public static Arguments parse(String[] args) {
        if (args.length != EXPECTED_ARGUMENTS) {
            throw new C43Exception(USAGE);
        }
        return new Arguments(pathOf(args[0]), pathOf(args[1]), pathOf(args[2]), pathOf(args[3]));
    }

    private static Path pathOf(String value) {
        try {
            return Path.of(value);
        } catch (InvalidPathException e) {
            throw new C43Exception("invalid path: '" + value + "'", e);
        }
    }
}
