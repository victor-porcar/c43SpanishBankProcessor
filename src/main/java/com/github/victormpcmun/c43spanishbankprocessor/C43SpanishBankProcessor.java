package com.github.victormpcmun.c43spanishbankprocessor;

import com.github.victormpcmun.c43spanishbankprocessor.model.Categorization;
import com.github.victormpcmun.c43spanishbankprocessor.model.CategoryDefinitions;
import com.github.victormpcmun.c43spanishbankprocessor.model.Deduplication;
import com.github.victormpcmun.c43spanishbankprocessor.model.DefinitionCsvReader;
import com.github.victormpcmun.c43spanishbankprocessor.model.MovementCategorizer;
import com.github.victormpcmun.c43spanishbankprocessor.model.MovementDeduplicator;
import com.github.victormpcmun.c43spanishbankprocessor.model.MovementLine;
import com.github.victormpcmun.c43spanishbankprocessor.model.MovementLineExtractor;
import com.github.victormpcmun.c43spanishbankprocessor.model.ResultRowAggregator;
import com.github.victormpcmun.c43spanishbankprocessor.parser.C43FileReader;
import com.github.victormpcmun.c43spanishbankprocessor.parser.C43Files;
import com.github.victormpcmun.c43spanishbankprocessor.parser.C43LineParser;
import com.github.victormpcmun.c43spanishbankprocessor.parser.MovementGrouper;
import com.github.victormpcmun.c43spanishbankprocessor.parser.RecordGroup;
import com.github.victormpcmun.c43spanishbankprocessor.printer.RecordLineFormatter;
import com.github.victormpcmun.c43spanishbankprocessor.writer.LogWriter;
import com.github.victormpcmun.c43spanishbankprocessor.writer.ResultCsvWriter;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class C43SpanishBankProcessor {

    static final String SUCCESS_MESSAGE = "SUCCESFUL EXECUTION";
    static final String ERROR_MESSAGE = "ERROR -> see logs";

    /** Everything went well. */
    static final int SUCCESS = 0;

    /** Something went wrong while processing; the log file tells what. */
    static final int ERROR = 1;

    /** The arguments themselves are wrong, so there is no log file to write to. */
    static final int WRONG_ARGUMENTS = 2;

    private C43SpanishBankProcessor() {
    }

    public static void main(String[] args) {
        Arguments arguments = argumentsOrExit(args);
        try {
            process(arguments);
            System.out.println(SUCCESS_MESSAGE);
        } catch (RuntimeException e) {
            reportError(arguments.logPath(), e);
            System.exit(ERROR);
        }
    }

    /** Without valid arguments the log file is unknown, so the problem is told on the console. */
    private static Arguments argumentsOrExit(String[] args) {
        try {
            return Arguments.parse(args);
        } catch (C43Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(WRONG_ARGUMENTS);
            throw e;
        }
    }

    private static void process(Arguments arguments) {
        Deduplication movements = new MovementDeduplicator()
                .removeDuplicates(movementsOf(C43Files.matchingAll(arguments.c43Files())));
        CategoryDefinitions definitions =
                CategoryDefinitions.of(new DefinitionCsvReader().read(arguments.definitionPath()));
        Categorization categorization = new MovementCategorizer()
                .categorize(movements.movements(), definitions.categories(), definitions.defaultCategory());
        new ResultCsvWriter().write(arguments.resultPath(),
                new ResultRowAggregator().aggregate(categorization.rows()));
        new LogWriter().write(arguments.logPath(),
                movements.duplicates(), categorization.movementsWithoutCategory());
    }

    private static List<MovementLine> movementsOf(List<Path> files) {
        MovementLineExtractor extractor = new MovementLineExtractor();
        RecordLineFormatter formatter = new RecordLineFormatter();
        List<RecordGroup> groups = new MovementGrouper().group(new C43FileReader(new C43LineParser()).read(files));
        return groups.stream()
                .map(formatter::format)
                .map(extractor::extract)
                .flatMap(Optional::stream)
                .toList();
    }

    /** The console only says that it failed; the log file holds the exception. */
    private static void reportError(Path logPath, RuntimeException error) {
        try {
            new LogWriter().writeError(logPath, error);
        } catch (RuntimeException cannotLog) {
            System.err.println("Error: " + error.getMessage());
            System.err.println("The log file could not be written either: " + cannotLog.getMessage());
        }
        System.out.println(ERROR_MESSAGE);
    }
}
