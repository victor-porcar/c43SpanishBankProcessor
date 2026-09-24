package com.github.victormpcmun.c43spanishbankprocessor;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Command line arguments: the C43 files to read, the result file, the CSV of categories and the
 * log file.
 *
 * <p>The last three arguments are the three files; everything before them is taken as C43 files.
 * That way a wildcard works whether it reaches the program as written, such as "D:\C43\*.txt", or
 * already turned into one argument per file, which is what the Windows java launcher does with it.
 * The C43 files are kept as text, not as paths, because *.txt is not a valid path name on Windows.
 */
public record Arguments(List<String> c43Files, Path resultPath, Path definitionPath, Path logPath) {

    private static final int FILES_AT_THE_END = 3;
    private static final int MINIMUM_ARGUMENTS = FILES_AT_THE_END + 1;

    private static final String USAGE = """
            expected at least 4 arguments
              1. C43 file to read, or several of a directory with wildcards: "D:\\C43\\originales\\*.txt"
                 (several files, one after another, are accepted as well)
              2. Result csv file
              3. CSV file with the categories: category;subcategory;definition
                 (the line whose definition is *** gives the category of the movements that match no other)
              4. Log file, where the movements that match no category are written
            example:
              java -jar dist/c43SpanishBankProcessor.jar "D:\\bank\\*.txt" "D:\\bank\\result.csv" \
            "D:\\bank\\categories.csv" "D:\\bank\\process.log\"""";

    public Arguments {
        c43Files = List.copyOf(c43Files);
    }

    public static Arguments parse(String[] args) {
        if (args.length < MINIMUM_ARGUMENTS) {
            throw new C43Exception(USAGE);
        }
        int firstOfTheEnd = args.length - FILES_AT_THE_END;
        return new Arguments(
                List.of(Arrays.copyOfRange(args, 0, firstOfTheEnd)),
                pathOf(args[firstOfTheEnd]),
                pathOf(args[firstOfTheEnd + 1]),
                pathOf(args[firstOfTheEnd + 2]));
    }

    private static Path pathOf(String value) {
        try {
            return Path.of(value);
        } catch (InvalidPathException e) {
            throw new C43Exception("invalid path: '" + value + "'", e);
        }
    }
}
