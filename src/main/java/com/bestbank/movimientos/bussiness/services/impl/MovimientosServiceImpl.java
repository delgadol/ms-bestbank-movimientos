package com.bestbank.movimientos.bussiness.services.impl;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bestbank.movimientos.bussiness.dto.req.InfoTransacionReq;
import com.bestbank.movimientos.bussiness.dto.res.SaldoRes;
import com.bestbank.movimientos.bussiness.dto.res.TransaccionRes;
import com.bestbank.movimientos.bussiness.services.MovimientosService;
import com.bestbank.movimientos.bussiness.services.ProductoApiClientService;
import com.bestbank.movimientos.bussiness.utils.ModelMapperUtils;
import com.bestbank.movimientos.domain.model.Transaccion;
import com.bestbank.movimientos.domain.repositories.MovimientosRepository;
import com.bestbank.movimientos.domain.repositories.SaldoRespository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class MovimientosServiceImpl implements MovimientosService{

  private final ProductoApiClientService servProdApi;
  
  private final MovimientosRepository servMovRepo;
  
  private final SaldoRespository servSaldoRepo;
  
  private final ReactiveMongoOperations mongoOperations;
  
  public MovimientosServiceImpl(ProductoApiClientService api, 
      MovimientosRepository servMovRepo, SaldoRespository servRepoSaldo, 
      ReactiveMongoOperations mongoOperations) {
    super();
    this.servProdApi = api;
    this.servMovRepo = servMovRepo;
    this.servSaldoRepo = servRepoSaldo;
    this.mongoOperations = mongoOperations;
  }

  
  /**
   * Obtiene el saldo de un producto específico.
   *
   * @param idProducto el identificador del producto
   * @return un Mono que emite el objeto SaldoRes del producto
   */
  @Override
  public Mono<SaldoRes> getProductBalance(String idProdcuto) {
    return servProdApi.getProducto(idProdcuto)
        .flatMap(prodApi ->{
          return servSaldoRepo.findFirstByCodigoProducto(idProdcuto)
              .flatMap(entidad -> {
                return Mono.just(ModelMapperUtils.map(entidad, SaldoRes.class));
              });
        });
  }

  /**
   * Obtiene todos los saldos relacionados a un cliente dado.
   *
   * @param idCliente el identificador del cliente
   * @return un Flux que emite objetos SaldoRes
   */
  @Override
  public Flux<SaldoRes> getAllBalanceByClientId(String idCliente) {
    return servProdApi.getCliente(idCliente)
        .flux()
        .flatMap(clienteApi -> {
          return servSaldoRepo.findAllByIdPersona(idCliente)
              .flatMap(saldoProd -> {
                return Mono.just(ModelMapperUtils.map(saldoProd, SaldoRes.class)).flux();
              })
              .switchIfEmpty(Flux.empty());
        });
  }
  
  /**
   * Obtiene todas las transacciones relacionadas a un producto dado.
   *
   * @param idProducto el identificador del producto
   * @return un Flux que emite objetos TransaccionRes
   */
  @Override
  public Flux<TransaccionRes> getAllTransaccionByProductID(String idProducto) {
    return servProdApi.getProducto(idProducto)
        .flux()
        .flatMap(productoApi -> {
          return mongoOperations.find(servMovRepo.getDatosPorCodigoYFechaActualY3MesesAtrasQuery(idProducto),Transaccion.class)
              .flatMap(entidad -> {
                return Mono.just(ModelMapperUtils.map(entidad, TransaccionRes.class)).flux();
              })
              .switchIfEmpty(Flux.empty());
        });
  }

  @Override
  public Mono<TransaccionRes> postTransaccion(InfoTransacionReq transaccion) {
    // TODO Auto-generated method stub
    return null;
  }

}
