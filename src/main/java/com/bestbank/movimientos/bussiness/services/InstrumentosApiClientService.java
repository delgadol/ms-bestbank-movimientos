package com.bestbank.movimientos.bussiness.services;

/**
 * Interfaz que define los métodos para interactuar con el cliente API de Instrumentos.
 * Proporciona operaciones para realizar solicitudes y recibir respuestas 
 * relacionadas con instrumentos
 * desde un servicio o API externo.
 */

import com.bestbank.movimientos.bussiness.dto.res.InstrumentoAsoRes;

import reactor.core.publisher.Mono;

public interface InstrumentosApiClientService {
  
  Mono<InstrumentoAsoRes> getInstrumentoInfo(String idInstrumento);

}
