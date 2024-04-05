package com.tech.haven.configurations;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomConfigurations {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

	@Bean
    Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }

}
