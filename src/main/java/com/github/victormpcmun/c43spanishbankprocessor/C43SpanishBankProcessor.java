package com.github.victormpcmun.c43spanishbankprocessor;

import com.github.victormpcmun.c43spanishbankprocessor.model.Categorization;
import com.github.victormpcmun.c43spanishbankprocessor.model.DefinitionCsvReader;
import com.github.victormpcmun.c43spanishbankprocessor.model.DefinitionForCategorySubcategory;
import com.github.victormpcmun.c43spanishbankprocessor.model.MovementLine;
import com.github.victormpcmun.c43spanishbankprocessor.model.MovementCategorizer;
import com.github.victormpcmun.c43spanishbankprocessor.model.MovementLineExtractor;
import com.github.victormpcmun.c43spanishbankprocessor.model.ResultRowAggregator;
import com.github.victormpcmun.c43spanishbankprocessor.parser.C43FileReader;
import com.github.victormpcmun.c43spanishbankprocessor.parser.C43LineParser;
import com.github.victormpcmun.c43spanishbankprocessor.parser.MovementGrouper;
import com.github.victormpcmun.c43spanishbankprocessor.parser.RecordGroup;
import com.github.victormpcmun.c43spanishbankprocessor.printer.DefinitionPrinter;
import com.github.victormpcmun.c43spanishbankprocessor.printer.MovementLinePrinter;
import com.github.victormpcmun.c43spanishbankprocessor.printer.RecordLineFormatter;
import com.github.victormpcmun.c43spanishbankprocessor.writer.LogWriter;
import com.github.victormpcmun.c43spanishbankprocessor.writer.ResultCsvWriter;

import java.io.Console;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class C43SpanishBankProcessor {

    private C43SpanishBankProcessor() {
    }

    public static void main(String[] args) {
        try {
            run(Arguments.parse(args));
        } catch (C43Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void run(Arguments arguments) {
        List<MovementLine> movements = movementsOf(arguments.c43File());
        List<DefinitionForCategorySubcategory> definitions =
                new DefinitionCsvReader().read(arguments.definitionPath());
        print(movements, definitions);

        Categorization categorization =
                new MovementCategorizer().categorize(movements, definitions, arguments.defaultCategory());
        new ResultCsvWriter().write(arguments.resultPath(),
                new ResultRowAggregator().aggregate(categorization.rows()));
        new LogWriter().write(arguments.logPath(), categorization.movementsWithoutCategory());
    }

    private static List<MovementLine> movementsOf(Path file) {
        MovementLineExtractor extractor = new MovementLineExtractor();
        RecordLineFormatter formatter = new RecordLineFormatter();
        List<RecordGroup> groups = new MovementGrouper().group(new C43FileReader(new C43LineParser()).read(file));
        return groups.stream()
                .map(formatter::format)
                .map(extractor::extract)
                .flatMap(Optional::stream)
                .toList();
    }

    private static void print(List<MovementLine> movements, List<DefinitionForCategorySubcategory> definitions) {
        PrintStream out = consoleOutput();
        MovementLinePrinter movementPrinter = new MovementLinePrinter(out);
        for (int index = 0; index < movements.size(); index++) {
            movementPrinter.print(index + 1, movements.get(index));
        }
        DefinitionPrinter definitionPrinter = new DefinitionPrinter(out);
        for (int index = 0; index < definitions.size(); index++) {
            definitionPrinter.print(index + 1, definitions.get(index));
        }
    }

    /** Standard output in the console's own charset, so accents show right in a Windows console. */
    private static PrintStream consoleOutput() {
        Console console = System.console();
        Charset charset = console != null ? console.charset() : Charset.defaultCharset();
        return new PrintStream(new FileOutputStream(FileDescriptor.out), true, charset);
    }
}
