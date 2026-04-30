package edu.eci.cvds.ecireserves.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
            .info(new Info()
                .title("EciReserves API")
                .version("1.0")
                .description("Documentación de la API del sistema de reservas de laboratorios en la Escuela Colombiana de Ingeniería."));
    }
}
