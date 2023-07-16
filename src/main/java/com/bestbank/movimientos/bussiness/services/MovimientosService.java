package com.bestbank.movimientos.bussiness.services;

import com.bestbank.movimientos.bussiness.dto.req.InfoTransacionReq;
import com.bestbank.movimientos.bussiness.dto.res.SaldoRes;
import com.bestbank.movimientos.bussiness.dto.res.TransaccionRes;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovimientosService {
  
  public Mono<SaldoRes> getProductBalance(String idProdcuto); 
  
  public Flux<SaldoRes> getAllBalanceByClientId(String idCliente);
  
  public Flux<TransaccionRes> getAllTransaccionByProductID(String idProducto);  
  
  public Mono<TransaccionRes> postTransaccion(InfoTransacionReq transaccion);
  
  
}
