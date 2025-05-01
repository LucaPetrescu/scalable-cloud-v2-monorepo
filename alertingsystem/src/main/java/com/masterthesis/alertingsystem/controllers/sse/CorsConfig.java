package com.masterthesis.alertingsystem.controllers.sse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return registry -> registry.addMapping("/see/**").allowedOrigins().allowedMethods("GET");
    }


}
