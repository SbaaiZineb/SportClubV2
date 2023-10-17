package com.sportclub.sportclub.tools;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateFormatter implements Formatter<LocalDate> {
    private final DateTimeFormatter formatter;

    public LocalDateFormatter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }
    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        return LocalDate.parse(text, formatter);
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return object.format(formatter);
    }
}
