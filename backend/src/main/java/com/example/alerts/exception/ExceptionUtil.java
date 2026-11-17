package com.example.alerts.exception;

public final class ExceptionUtil {

    private ExceptionUtil() {
    }

    public static ConflictException duplicateCode(String resource, String code) {
        return new ConflictException("A " + resource + " with code '" + code + "' already exists.");
    }
}
