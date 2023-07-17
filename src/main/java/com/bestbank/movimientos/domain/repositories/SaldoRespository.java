package com.bestbank.movimientos.domain.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bestbank.movimientos.domain.model.Saldo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interfaz que define un repositorio de saldos.
 * Proporciona métodos para acceder y gestionar los saldos en una base de datos reactiva.
 * Esta interfaz extiende ReactiveMongoRepository y trabaja con entidades de tipo Saldo.
 */
public interface SaldoRespository extends ReactiveMongoRepository<Saldo, String> {
  
  Mono<Saldo> findFirstByCodigoProducto(String codigoProducto);
  
  
  Flux<Saldo> findAllByIdPersona(String idPersona);

}
