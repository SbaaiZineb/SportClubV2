package com.sportclub.sportclub.tools;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new LocalDateFormatter("yyyy-MM-dd")); // Replace with your desired date format
        registry.addFormatter(new LocalTimeFormatter("HH:mm")); // Replace with your desired time format
    }

}
