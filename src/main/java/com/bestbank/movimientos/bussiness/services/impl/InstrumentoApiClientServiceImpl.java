package com.bestbank.movimientos.bussiness.services.impl;

import com.bestbank.movimientos.bussiness.client.WebClientApi;
import com.bestbank.movimientos.bussiness.dto.res.InstrumentoAsoRes;
import com.bestbank.movimientos.bussiness.services.InstrumentosApiClientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementación de la interfaz InstrumentosApiClientService que permite interactuar con el cliente API de Instrumentos.
 * Proporciona métodos para realizar solicitudes y recibir respuestas relacionadas con instrumentos
 * desde un servicio o API externo.
 */
@Log4j2
@Service
public class InstrumentoApiClientServiceImpl implements InstrumentosApiClientService {
  
  
  private final WebClientApi webClientApi;

  public InstrumentoApiClientServiceImpl(WebClientApi webClientApi) {
    this.webClientApi = webClientApi;
  }

  @Value("${app.instrumentoUrl}")
  private String instrumentoUrl;
  
  @Value("${app.instrumentoUrlAso}")
  private String instrumentoUrlAso;
  
  @Override
  public Mono<InstrumentoAsoRes> getInstrumentoInfo(String idInstrumento) {
    log.info(String.format("Consultando Api Instreumento - Roles : %s", idInstrumento));
    return webClientApi.getMono(instrumentoUrl, String.format(this.instrumentoUrlAso, idInstrumento), 
        InstrumentoAsoRes.class, String.format("Error al Buscar Producto Rol : %s", idInstrumento));
  }

}
