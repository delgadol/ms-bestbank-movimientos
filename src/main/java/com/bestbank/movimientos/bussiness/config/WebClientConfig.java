package com.bestbank.movimientos.bussiness.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Clase de configuración que define y configura un WebClient para realizar solicitudes 
 * HTTP a servicios web externos.
 * Esta clase está anotada con @Configuration para indicar que es una clase de 
 * configuración de Spring.
 */
@Configuration
public class WebClientConfig {

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }
  
}