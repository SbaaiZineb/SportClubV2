package com.sportclub.sportclub.tools;


import com.sportclub.sportclub.service.FingerprintSensorEx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZKFingerprintConfiguration {
    @Bean
    public FingerprintSensorEx fingerprintSensorEx() {
        // Initialize and configure the FingerprintSensorEx object
        return new FingerprintSensorEx();
    }
}
