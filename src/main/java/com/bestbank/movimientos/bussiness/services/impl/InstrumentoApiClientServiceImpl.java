package com.bestbank.movimientos.bussiness.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bestbank.movimientos.bussiness.client.WebClientApi;
import com.bestbank.movimientos.bussiness.dto.res.InstrumentoAsoRes;
import com.bestbank.movimientos.bussiness.dto.res.ProductoRolesRes;
import com.bestbank.movimientos.bussiness.services.InstrumentosApiClientService;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class InstrumentoApiClientServiceImpl implements InstrumentosApiClientService {

  @Value("${app.instrumentoUrl}")
  private String instrumentoUrl;
  
  @Override
  public Mono<InstrumentoAsoRes> getInstrumentoInfo(String idInstrumento) {
    log.info(String.format("Consultando Api Instreumento - Roles : %s", idInstrumento));
    return WebClientApi.getMono(String.format(this.instrumentoUrl, idInstrumento), 
        InstrumentoAsoRes.class, String.format("Error al Buscar Producto Rol : %s", idInstrumento));
  }

}
