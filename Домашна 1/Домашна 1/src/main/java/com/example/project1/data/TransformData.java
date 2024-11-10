package com.example.project1.data;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TransformData {

    public static LocalDate dateParser(String text, String pattern) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(text, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            return null;
        }
    }

    public static Double doubleParser(String text, NumberFormat format) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return format.parse(text).doubleValue();
        } catch (ParseException e) {
            System.out.println("Error parsing double: " + e.getMessage());
            return null;
        }
    }

    public static Integer integerParser(String text, NumberFormat format) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return format.parse(text).intValue();
        } catch (ParseException e) {
            System.out.println("Error parsing integer: " + e.getMessage());
            return null;
        }
    }

}
