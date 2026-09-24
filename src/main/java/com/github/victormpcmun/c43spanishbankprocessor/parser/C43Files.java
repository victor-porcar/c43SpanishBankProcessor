package com.github.victormpcmun.c43spanishbankprocessor.parser;

import com.github.victormpcmun.c43spanishbankprocessor.C43Exception;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The C43 files to process, named one by one or with wildcards in the file name, such as
 * D:\C43\originales\*.txt. Wildcards are looked for in that directory only, not in its
 * subdirectories, and the files are taken in the order of their names.
 */
public final class C43Files {

    private static final String WILDCARDS = "*?";
    private static final String SEPARATORS = "\\/";
    private static final String CURRENT_DIRECTORY = ".";

    private C43Files() {
    }

    /** Every file named by the given paths or patterns, without repeating any of them. */
    public static List<Path> matchingAll(List<String> pathsOrPatterns) {
        return pathsOrPatterns.stream()
                .map(C43Files::matching)
                .flatMap(List::stream)
                .distinct()
                .toList();
    }

    public static List<Path> matching(String pathOrPattern) {
        if (pathOrPattern == null || pathOrPattern.isBlank()) {
            throw new C43Exception("no C43 file given");
        }
        String fileName = fileNameOf(pathOrPattern);
        if (!hasWildcards(fileName)) {
            return List.of(pathOf(pathOrPattern));
        }
        return filesMatching(pathOf(directoryOf(pathOrPattern)), fileName, pathOrPattern);
    }

    private static List<Path> filesMatching(Path directory, String fileNamePattern, String pattern) {
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> found = Files.newDirectoryStream(directory, fileNamePattern)) {
            found.forEach(file -> {
                if (Files.isRegularFile(file)) {
                    files.add(file);
                }
            });
        } catch (IOException e) {
            throw new C43Exception("cannot list the C43 files of " + directory.toAbsolutePath(), e);
        }
        if (files.isEmpty()) {
            throw new C43Exception("no C43 file matches " + pattern);
        }
        files.sort(Comparator.comparing(file -> file.getFileName().toString()));
        return List.copyOf(files);
    }

    private static boolean hasWildcards(String fileName) {
        return fileName.chars().anyMatch(character -> WILDCARDS.indexOf(character) >= 0);
    }

    private static String fileNameOf(String pathOrPattern) {
        return pathOrPattern.substring(lastSeparatorOf(pathOrPattern) + 1);
    }

    /** What comes before the file name; just a dot when the pattern is only a file name. */
    private static String directoryOf(String pathOrPattern) {
        int separator = lastSeparatorOf(pathOrPattern);
        return separator < 0 ? CURRENT_DIRECTORY : pathOrPattern.substring(0, separator + 1);
    }

    private static int lastSeparatorOf(String pathOrPattern) {
        int last = -1;
        for (int index = 0; index < pathOrPattern.length(); index++) {
            if (SEPARATORS.indexOf(pathOrPattern.charAt(index)) >= 0) {
                last = index;
            }
        }
        return last;
    }

    private static Path pathOf(String value) {
        try {
            return Path.of(value);
        } catch (InvalidPathException e) {
            throw new C43Exception("invalid path: '" + value + "'", e);
        }
    }
}
