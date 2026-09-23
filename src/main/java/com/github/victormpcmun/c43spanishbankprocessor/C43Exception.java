package com.github.victormpcmun.c43spanishbankprocessor;

/**
 * Any problem that stops the processing, with a message meant for the user.
 */
public class C43Exception extends RuntimeException {

    public C43Exception(String message) {
        super(message);
    }

    public C43Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
