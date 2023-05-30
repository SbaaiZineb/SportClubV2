package com.sportclub.sportclub.tools;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalTimeFormatter implements Formatter<LocalTime> {
    private final DateTimeFormatter formatter;

    public LocalTimeFormatter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public LocalTime parse(String text, Locale locale) throws ParseException {
        return LocalTime.parse(text, formatter);
    }

    @Override
    public String print(LocalTime object, Locale locale) {
        return object.format(formatter);
    }
}
