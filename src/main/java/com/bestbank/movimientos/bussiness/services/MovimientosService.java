package com.bestbank.movimientos.bussiness.services;


import com.bestbank.movimientos.bussiness.dto.req.InfoTransaccionInstReq;
import com.bestbank.movimientos.bussiness.dto.req.InfoTransaccionInternaReq;
import com.bestbank.movimientos.bussiness.dto.req.InfoTransacionReq;
import com.bestbank.movimientos.bussiness.dto.res.SaldoDiarioInfoRes;
import com.bestbank.movimientos.bussiness.dto.res.SaldoRes;
import com.bestbank.movimientos.bussiness.dto.res.TransaccionRes;
import com.bestbank.movimientos.domain.utils.TipoInstrumento;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interfaz que define los servicios relacionados con los movimientos.
 * Proporciona métodos para realizar operaciones y consultas relacionadas con los 
 * movimientos en el sistema.
 */
public interface MovimientosService {
  
  public Mono<SaldoRes> getProductBalance(String idProdcuto); 
  
  public Mono<SaldoRes> getProductBalanceByInstrument(String idInstrumento);
  
  public Flux<TransaccionRes> getAllTransaccionByInstrument(String idInstrumento);  
  
  public Flux<SaldoRes> getAllBalanceByClientId(String idCliente);
  
  public Flux<TransaccionRes> getAllTransaccionByProductId(String idProducto);  
  
  public Mono<TransaccionRes> postTransaccion(InfoTransacionReq transaccion,
      TipoInstrumento tipoInstrumento, String idInstrumento);
  
  public Mono<TransaccionRes> postTransaccionIntoBanck(InfoTransaccionInternaReq operacionInterna);
  
  public Mono<SaldoDiarioInfoRes> getInformSaldosByIdProducto(String idProducto);
  
  public Mono<TransaccionRes> getAllTaxByIdProduct(String idProducto);
  
  public Mono<TransaccionRes> postTransaccionByInstrumento(InfoTransaccionInstReq transaccion);
  
  public Flux<TransaccionRes> getAllLastTraccionByProductId(String idProducto);
  
}
