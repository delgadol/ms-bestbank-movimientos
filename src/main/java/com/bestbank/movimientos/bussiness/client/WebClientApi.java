package com.bestbank.movimientos.bussiness.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class WebClientApi {
  
  private static final WebClient webClient  = WebClient.builder().build();

    
  public static <T> Mono<T> getMono(String url, Class<T> responseType, String errorID) {
    log.info(url);
    return webClient.get()
      .uri(url)
      .retrieve()
      .bodyToMono(responseType)
      .doOnError(WebClientResponseException.class, e -> {
        HttpStatus statusCode = (HttpStatus) e.getStatusCode();
        log.error(String.format("Error Api = URL: %s ,CODIGO: %s ,CONTRLID: %s", url,statusCode,errorID));
      });
  }

  public static <T> Flux<T> getFlux(String url, Class<T> responseType, String errorID) {
    log.info(url);
    return webClient.get()
      .uri(url)
      .retrieve()
      .bodyToFlux(responseType)
      .doOnError(WebClientResponseException.class, e -> {
        HttpStatus statusCode = (HttpStatus) e.getStatusCode();
        log.error(String.format("Error Api = URL: %s ,CODIGO: %s ,CONTRLID: %s", url,statusCode,errorID));
      });
  }
  

}
