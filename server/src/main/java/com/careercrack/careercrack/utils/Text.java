package com.careercrack.careercrack.utils;

public final class Text {

    private Text() {
    }

    /** Strips surrounding spaces and returns null if nothing is left, so "" and "   " are stored as no value. */
    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String stripped = value.strip();
        return stripped.isEmpty() ? null : stripped;
    }
}
