package com.edpsun.st.monitor.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

public class ObjectMapperProvider {
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
