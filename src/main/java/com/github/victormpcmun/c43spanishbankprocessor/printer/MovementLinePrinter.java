package com.github.victormpcmun.c43spanishbankprocessor.printer;

import com.github.victormpcmun.c43spanishbankprocessor.model.MovementLine;

import java.io.PrintStream;

/**
 * Prints a movement showing its four fields, one per line.
 */
public class MovementLinePrinter {

    private final PrintStream out;

    public MovementLinePrinter(PrintStream out) {
        this.out = out;
    }

    public void print(int number, MovementLine movement) {
        out.printf("Movement %d%n", number);
        out.printf("  year      : %s%n", movement.getYear());
        out.printf("  month     : %s%n", movement.getMonth());
        out.printf("  importe   : %s%n", movement.getImporte());
        out.printf("  plainLine : %s%n", movement.getPlainLine());
        out.println();
    }
}
