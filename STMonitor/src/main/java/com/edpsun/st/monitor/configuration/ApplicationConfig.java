package com.edpsun.st.monitor.configuration;

import com.edpsun.st.monitor.Monitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public Monitor getMonitor(){
        return new Monitor();
    }
}
