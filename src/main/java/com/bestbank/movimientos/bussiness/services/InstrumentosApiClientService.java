package com.bestbank.movimientos.bussiness.services;

import com.bestbank.movimientos.bussiness.dto.res.InstrumentoAsoRes;

import reactor.core.publisher.Mono;

public interface InstrumentosApiClientService {
  
  Mono<InstrumentoAsoRes> getInstrumentoInfo(String idInstrumento);

}
