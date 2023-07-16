package com.bestbank.movimientos.bussiness.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bestbank.movimientos.bussiness.dto.req.InfoTransaccionInternaReq;
import com.bestbank.movimientos.bussiness.dto.req.InfoTransacionReq;
import com.bestbank.movimientos.bussiness.dto.res.SaldoRes;
import com.bestbank.movimientos.bussiness.dto.res.TransaccionRes;
import com.bestbank.movimientos.bussiness.services.MovimientosService;
import com.bestbank.movimientos.bussiness.services.ProductoApiClientService;
import com.bestbank.movimientos.bussiness.utils.ModelMapperUtils;
import com.bestbank.movimientos.bussiness.utils.TransaccionesUtils;
import com.bestbank.movimientos.domain.model.Saldo;
import com.bestbank.movimientos.domain.model.Transaccion;
import com.bestbank.movimientos.domain.repositories.MovimientosRepository;
import com.bestbank.movimientos.domain.repositories.SaldoRespository;
import com.bestbank.movimientos.domain.utils.ResultadoTransaccion;
import com.bestbank.movimientos.domain.utils.TipoComision;
import com.bestbank.movimientos.domain.utils.TipoOperacion;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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
    return servProdApi.getProductoRoles(transaccion.getIdProducto())
        .filter(prodRolApiF1 -> TransaccionesUtils.clienteAutorizado(transaccion, prodRolApiF1))
        .flatMap(prodRolApi -> {
          return mongoOperations.count(servMovRepo.getDatosDeEsteMesQuery(prodRolApi.getId()), Transaccion.class)
            .filter(countAny -> true)
            .flatMap( numOptmes -> {
              log.info(String.format("Numero Opeaciones mes : %d", numOptmes));
              /** Verificamos el Saldo **/
              return getSaldoPorIdProd(transaccion.getIdProducto())
                  .flatMap(saldoActual -> {
                    Saldo nuevoSaldoReg = ModelMapperUtils.map(saldoActual, Saldo.class);
                    log.info(String.format("Saldo Actual : %.2f", nuevoSaldoReg.getSaldoActual()));
                    Transaccion nuevaTransaccion = TransaccionesUtils.getRegistroOperacion(transaccion, prodRolApi);
                    nuevaTransaccion.setSaldoInicial(nuevoSaldoReg.getSaldoActual());
                    nuevaTransaccion.setSaldoFinal(nuevoSaldoReg.getSaldoActual());
                    Transaccion nuevaComision = TransaccionesUtils.getRegistroOperacion(transaccion, prodRolApi);
                    List<Transaccion> listaTransacciones = new ArrayList<>();
                    Double comision = TransaccionesUtils.getComision(numOptmes, prodRolApi);
                    Double nuevoSaldo = TransaccionesUtils.nuevoSaldo(
                        transaccion.getTipoOperacion(), nuevoSaldoReg.getSaldoActual(), comision, transaccion.getMontoOperacion());
                    if (nuevoSaldo >= 0.00D ) {
                      log.info(String.format("Transaccion esta %s", ResultadoTransaccion.APROBADA));
                      nuevaTransaccion.setResultadoTransaccion(ResultadoTransaccion.APROBADA);
                      nuevaTransaccion.setSaldoFinal(nuevoSaldo + comision);
                      nuevoSaldoReg.setSaldoActual(nuevoSaldo);
                    }
                    listaTransacciones.add(nuevaTransaccion);
                    if (comision > 0.00D) {
                      nuevaComision.setMontoTransaccion(comision);
                      nuevaComision.setCodigoOperacion(TipoOperacion.CARGO);
                      nuevaComision.setSaldoInicial(nuevoSaldo + comision);
                      nuevaComision.setSaldoFinal(nuevoSaldo);
                      nuevaComision.setObservacionTransaccion(TipoComision.COMISION_LIMITE_OPERACION.toString());
                      nuevaComision.setResultadoTransaccion(ResultadoTransaccion.APROBADA);
                      listaTransacciones.add(nuevaComision);
                    }
                    return servSaldoRepo.save(nuevoSaldoReg)
                        .flatMap(saldoDB -> {
                          return servMovRepo.saveAll(listaTransacciones)
                              .take(1)
                              .single()
                              .flatMap( item -> {
                                return Mono.just(ModelMapperUtils.map(item, TransaccionRes.class));
                              });
                        });
                  });
            });
        });
  }
  
  @Override
  public Mono<TransaccionRes> postTransaccionIntoBanck(InfoTransaccionInternaReq operacionInterna) {
    
    if (operacionInterna.getIdProducto().contains(operacionInterna.getIdProducto2())) {
      throw new DuplicateKeyException("Productos deben ser diferentes");
    }
    /** Esta es la operacion de cargo */
    InfoTransacionReq outTransaccion = new InfoTransacionReq();
    outTransaccion.setIdProducto(operacionInterna.getIdProducto());
    outTransaccion.setCodPersona(operacionInterna.getCodPersona());
    outTransaccion.setTipoOperacion(TipoOperacion.CARGO);
    outTransaccion.setMontoOperacion(operacionInterna.getMontoOperacion());
    outTransaccion.setObervacionTransaccion(operacionInterna.getObervacionTransaccion());
    /** esta es la operacion de abono */
    InfoTransacionReq inTransaccion = new InfoTransacionReq();
    inTransaccion.setIdProducto(operacionInterna.getIdProducto2());
    inTransaccion.setCodPersona(operacionInterna.getCodPersona());
    inTransaccion.setTipoOperacion(TipoOperacion.ABONO);
    inTransaccion.setMontoOperacion(operacionInterna.getMontoOperacion());
    inTransaccion.setObervacionTransaccion(operacionInterna.getObervacionTransaccion());
    /** lanzamos la ejecuion por funcion de transacciones **/
    return servProdApi.getProducto(outTransaccion.getIdProducto())
        .flatMap( prodOut -> {
          return servProdApi.getProducto(inTransaccion.getIdProducto())
              .flatMap(prodIn -> {
                return postTransaccion(outTransaccion)
                    .filter(outTransRes -> 
                    outTransRes.getResultadoTransaccion()==ResultadoTransaccion.APROBADA)
                    .flatMap( transOut -> {
                      return postTransaccion(inTransaccion)
                          .filter(inTransRes -> 
                          inTransRes.getResultadoTransaccion()==ResultadoTransaccion.APROBADA)
                          .flatMap( transIn -> {
                            return Mono.just(transOut);
                          })
                          .switchIfEmpty(rollBackTransaccion(outTransaccion));
                    })
                    .switchIfEmpty(
                        Mono.error( new RuntimeException("Transaccion Fallida Cuenta Emisora")));
              });
        });
    
  }
  
  /** rollback transaccion **/
  
  public Mono<TransaccionRes> rollBackTransaccion(InfoTransacionReq transaccion){
    return postTransaccion(transaccion);
    
  }
  
  /** Buscar Saldo Actual **/
  
  private Mono<Saldo> getSaldoPorIdProd(String idProducto) {
    return servSaldoRepo.findFirstByCodigoProducto(idProducto)
    .flatMap(saldoActual -> {
      return Mono.just(saldoActual);      
    });
  }
  
  /** Saldo Nuevo Saldo **/
  
  
  
  

}
