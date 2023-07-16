package com.bestbank.movimientos.bussiness.services;

import com.bestbank.movimientos.bussiness.dto.res.ClienteRes;
import com.bestbank.movimientos.domain.model.Producto;

import reactor.core.publisher.Mono;

public interface ProductoApiClientService {
  
  Mono<Producto> getProducto(String idProducto);
  
  Mono<ClienteRes> getCliente(String idCliente);

}
