package com.autoestufa.backend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Configura a comunicacao entre a API Spring e a aplicacao Angular.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Aplica as regras de CORS somente aos endpoints da API.
        registry.addMapping("/api/**")
                // Permite chamadas feitas pelo servidor local do Angular.
                .allowedOrigins("http://localhost:4200")
                // Metodos usados pelas operacoes de consulta e alteracao.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // Permite os cabecalhos enviados pelo frontend.
                .allowedHeaders("*");
    }
}