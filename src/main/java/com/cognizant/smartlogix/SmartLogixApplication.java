package com.cognizant.smartlogix;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class SmartLogixApplication {

    private static final Logger logger = LoggerFactory.getLogger(SmartLogixApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SmartLogixApplication.class, args);
    }

    @Bean
    public CommandLineRunner startupCheck() {
        return args -> {
            logger.info("================================================");
            logger.info("SmartLogix Last-Mile Orchestration is STARTING");
            logger.info("Base Package: com.cognizant.smartlogix");
            logger.info("================================================");
        };
    }
}