package com.bestbank.movimientos.bussiness.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bestbank.movimientos.bussiness.dto.DataTransaccionesDto;
import com.bestbank.movimientos.bussiness.dto.req.InfoTransaccionInternaReq;
import com.bestbank.movimientos.bussiness.dto.req.InfoTransacionReq;
import com.bestbank.movimientos.bussiness.dto.res.ProductoRolesRes;
import com.bestbank.movimientos.bussiness.dto.res.SaldoDiarioInfoRes;
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
import com.bestbank.movimientos.domain.utils.TipoInstrumento;
import com.bestbank.movimientos.domain.utils.TipoOperacion;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Transactional
public class MovimientosServiceImpl implements MovimientosService {

  private final ProductoApiClientService servProdApi;
  
  private final MovimientosRepository servMovRepo;
  
  private final SaldoRespository servSaldoRepo;
  
  private final ReactiveMongoOperations mongoOperations;
  
  /**
   * Constructor de la implementación de servicios de movimientos.
   *
   * @param api               
   Instancia de ProductoApiClientService para interactuar con el API de productos.
   * @param servMovRepo       
   Repositorio de movimientos para acceder y gestionar los movimientos en la base de datos.
   * @param servRepoSaldo     
   Repositorio de saldos para acceder y gestionar los saldos en la base de datos.
   * @param mongoOperations   
   Operaciones reactivas de MongoDB para realizar consultas y operaciones en la base de datos.
   */
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
  public Mono<SaldoRes> getProductBalance(String idProducto) {
    return servProdApi.getProducto(idProducto)
        .flatMap(prodApi -> 
          servSaldoRepo.findFirstByCodigoProducto(idProducto)
              .flatMap(entidad -> 
                Mono.just(ModelMapperUtils.map(entidad, SaldoRes.class))
              )
        );
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
        .flatMap(clienteApi -> 
          servSaldoRepo.findAllByIdPersona(idCliente)
              .flatMap(saldoProd -> 
                Mono.just(ModelMapperUtils.map(saldoProd, SaldoRes.class)).flux()
              )
              .switchIfEmpty(Flux.empty())
        );
  }
  
  /**
   * Obtiene todas las transacciones relacionadas a un producto dado.
   *
   * @param idProducto el identificador del producto
   * @return un Flux que emite objetos TransaccionRes
   */
  @Override
  public Flux<TransaccionRes> getAllTransaccionByProductId(String idProducto) {
    return servProdApi.getProducto(idProducto)
        .flux()
        .flatMap(productoApi -> 
           mongoOperations.find(
              servMovRepo.getDatosPorCodigoYFechaActualY3MesesAtrasQuery(idProducto),
              Transaccion.class)
              .flatMap(entidad -> 
                Mono.just(ModelMapperUtils.map(entidad, TransaccionRes.class)).flux()
              )
              .switchIfEmpty(Flux.empty())
        );
  }
  
  
  public Mono<TransaccionRes> postTransaccionByInstrumentId(InfoTransacionReq transaccion) {
    return null;
    
  }
  
  private DataTransaccionesDto getDataTransaccion(ProductoRolesRes prodRolApi, 
      Long numOptmes, Saldo saldoActual, InfoTransacionReq transaccion,
      TipoInstrumento tipoInstrumento, String idInstrumento) {
    Saldo nuevoSaldoReg = ModelMapperUtils.map(saldoActual, Saldo.class);
    Transaccion nuevaTransaccion = TransaccionesUtils.getRegistroOperacion(
        transaccion, prodRolApi);
    nuevaTransaccion.setSaldoInicial(nuevoSaldoReg.getSaldoActual());
    nuevaTransaccion.setSaldoFinal(nuevoSaldoReg.getSaldoActual());
    nuevaTransaccion.setTipoInstrumento(tipoInstrumento);
    nuevaTransaccion.setIdInstrumento(idInstrumento);
    Transaccion nuevaComision = TransaccionesUtils.getRegistroOperacion(
        transaccion, prodRolApi);
    nuevaTransaccion.setTipoInstrumento(TipoInstrumento.CANAL_POR_DEFECTO);
    nuevaTransaccion.setIdInstrumento("");
    List<Transaccion> listaTransacciones = new ArrayList<>();   
    Double comision = TransaccionesUtils.getComision(numOptmes, prodRolApi);
    Double nuevoSaldo = TransaccionesUtils.nuevoSaldo(
        transaccion.getTipoOperacion(), nuevoSaldoReg.getSaldoActual(), 
        comision, transaccion.getMontoOperacion());
    if (nuevoSaldo >= 0.00D) {
      log.info(String.format("Transaccion esta %s", ResultadoTransaccion.APROBADA));
      nuevaTransaccion.setResultadoTransaccion(ResultadoTransaccion.APROBADA);
      nuevaTransaccion.setSaldoFinal(nuevoSaldo + comision);
      nuevoSaldoReg.setSaldoActual(nuevoSaldo);
    }
    listaTransacciones.add(nuevaTransaccion);
    if (comision > 0.00D && nuevoSaldo >= 0.00D) {
      nuevaComision.setMontoTransaccion(comision);
      nuevaComision.setCodigoOperacion(TipoOperacion.CARGO);
      nuevaComision.setSaldoInicial(nuevoSaldo + comision);
      nuevaComision.setSaldoFinal(nuevoSaldo);
      nuevaComision.setObservacionTransaccion(
          TipoComision.COMISION_LIMITE_OPERACION.toString());
      nuevaComision.setResultadoTransaccion(ResultadoTransaccion.APROBADA);
      listaTransacciones.add(nuevaComision);
    }
    return new DataTransaccionesDto(nuevoSaldoReg, 
        nuevaTransaccion.getResultadoTransaccion(), listaTransacciones);    
  }
  
  
  /*
   * Crea una nueva transacción y la guarda en el sistema.
   * 
   * @param transaccion Objeto que contiene la información de la transacción a crear.
   * @return Mono que emite la respuesta de la transacción creada.
   */  
  @Override
  public Mono<TransaccionRes> postTransaccion(InfoTransacionReq transaccion) {
    return servProdApi.getProductoRoles(transaccion.getIdProducto())
        .filter(prodRolApiF1 -> TransaccionesUtils.clienteAutorizado(transaccion, prodRolApiF1))
        .flatMap(prodRolApi -> 
          mongoOperations.count(servMovRepo.getDatosDeEsteMesQuery(prodRolApi.getId()), 
              Transaccion.class)
            .filter(countAny -> true)
            .flatMap(numOptmes -> {
              log.info(String.format("Numero Opeaciones mes : %d", numOptmes));
              return getSaldoPorIdProd(transaccion.getIdProducto())
                  .flatMap(saldoActual -> {
                    DataTransaccionesDto dataTransacciones = getDataTransaccion(
                        prodRolApi, numOptmes, saldoActual, transaccion, 
                        TipoInstrumento.CANAL_POR_DEFECTO, "");
                    return servSaldoRepo.save(dataTransacciones.getNuevoSaldoReg())
                        .flatMap(saldoDB -> 
                          servMovRepo.saveAll(dataTransacciones.getNuevasTransacciones())
                              .take(1)
                              .single()
                              .flatMap(item -> 
                                Mono.just(ModelMapperUtils.map(item, TransaccionRes.class))
                              )
                        );
                  });
            })
        );
  }
  
//  @Override
//  public Mono<TransaccionRes> postTransaccion(InfoTransacionReq transaccion) {
//    return servProdApi.getProductoRoles(transaccion.getIdProducto())
//        .filter(prodRolApiF1 -> TransaccionesUtils.clienteAutorizado(transaccion, prodRolApiF1))
//        .flatMap(prodRolApi -> {
//          return mongoOperations.count(servMovRepo.getDatosDeEsteMesQuery(prodRolApi.getId()), 
//              Transaccion.class)
//            .filter(countAny -> true)
//            .flatMap(numOptmes -> {
//              log.info(String.format("Numero Opeaciones mes : %d", numOptmes));
//              return getSaldoPorIdProd(transaccion.getIdProducto())
//                  .flatMap(saldoActual -> {
//                    Saldo nuevoSaldoReg = ModelMapperUtils.map(saldoActual, Saldo.class);
//                    log.info(String.format("Saldo Actual : %.2f", nuevoSaldoReg.getSaldoActual()));
//                    Transaccion nuevaTransaccion = TransaccionesUtils.getRegistroOperacion(
//                        transaccion, prodRolApi);
//                    nuevaTransaccion.setSaldoInicial(nuevoSaldoReg.getSaldoActual());
//                    nuevaTransaccion.setSaldoFinal(nuevoSaldoReg.getSaldoActual());
//                    Transaccion nuevaComision = TransaccionesUtils.getRegistroOperacion(
//                        transaccion, prodRolApi);
//                    List<Transaccion> listaTransacciones = new ArrayList<>();
//                    Double comision = TransaccionesUtils.getComision(numOptmes, prodRolApi);
//                    Double nuevoSaldo = TransaccionesUtils.nuevoSaldo(
//                        transaccion.getTipoOperacion(), nuevoSaldoReg.getSaldoActual(), 
//                        comision, transaccion.getMontoOperacion());
//                    if (nuevoSaldo >= 0.00D) {
//                      log.info(String.format("Transaccion esta %s", ResultadoTransaccion.APROBADA));
//                      nuevaTransaccion.setResultadoTransaccion(ResultadoTransaccion.APROBADA);
//                      nuevaTransaccion.setSaldoFinal(nuevoSaldo + comision);
//                      nuevoSaldoReg.setSaldoActual(nuevoSaldo);
//                    }
//                    listaTransacciones.add(nuevaTransaccion);
//                    if (comision > 0.00D) {
//                      nuevaComision.setMontoTransaccion(comision);
//                      nuevaComision.setCodigoOperacion(TipoOperacion.CARGO);
//                      nuevaComision.setSaldoInicial(nuevoSaldo + comision);
//                      nuevaComision.setSaldoFinal(nuevoSaldo);
//                      nuevaComision.setObservacionTransaccion(
//                          TipoComision.COMISION_LIMITE_OPERACION.toString());
//                      nuevaComision.setResultadoTransaccion(ResultadoTransaccion.APROBADA);
//                      listaTransacciones.add(nuevaComision);
//                    }
//                    return servSaldoRepo.save(nuevoSaldoReg)
//                        .flatMap(saldoDB -> {
//                          return servMovRepo.saveAll(listaTransacciones)
//                              .take(1)
//                              .single()
//                              .flatMap(item -> {
//                                return Mono.just(ModelMapperUtils.map(item, TransaccionRes.class));
//                              });
//                        });
//                  });
//            });
//        });
//  }
  
  /**
   * Realiza una solicitud POST para realizar una transacción interna en el banco.
   *
   * @param operacionInterna La información de transacción interna.
   * @return Un Mono que emite la respuesta de la transacción realizada.
   */
  @Override
  public Mono<TransaccionRes> postTransaccionIntoBanck(InfoTransaccionInternaReq operacionInterna) {
    
    if (operacionInterna.getIdProducto().contains(operacionInterna.getIdProducto2())) {
      throw new DuplicateKeyException("Productos deben ser diferentes");
    }
    InfoTransacionReq outTransaccion = new InfoTransacionReq();
    outTransaccion.setIdProducto(operacionInterna.getIdProducto());
    outTransaccion.setCodPersona(operacionInterna.getCodPersona());
    outTransaccion.setTipoOperacion(TipoOperacion.CARGO);
    outTransaccion.setMontoOperacion(operacionInterna.getMontoOperacion());
    outTransaccion.setObervacionTransaccion(operacionInterna.getObervacionTransaccion());
    InfoTransacionReq inTransaccion = new InfoTransacionReq();
    inTransaccion.setIdProducto(operacionInterna.getIdProducto2());
    inTransaccion.setCodPersona(operacionInterna.getCodPersona());
    inTransaccion.setTipoOperacion(TipoOperacion.ABONO);
    inTransaccion.setMontoOperacion(operacionInterna.getMontoOperacion());
    inTransaccion.setObervacionTransaccion(operacionInterna.getObervacionTransaccion());
    return servProdApi.getProducto(outTransaccion.getIdProducto())
        .flatMap(prodOut ->  
          servProdApi.getProducto(inTransaccion.getIdProducto())
              .flatMap(prodIn -> 
                postTransaccion(outTransaccion)
                    .filter(outTransRes -> 
                    outTransRes.getResultadoTransaccion() == ResultadoTransaccion.APROBADA)
                    .flatMap(transOut -> 
                      postTransaccion(inTransaccion)
                          .filter(inTransRes -> 
                          inTransRes.getResultadoTransaccion() == ResultadoTransaccion.APROBADA)
                          .flatMap(transIn -> 
                            Mono.just(transOut)
                          )
                          .switchIfEmpty(rollBackTransaccion(outTransaccion))
                    )
                    .switchIfEmpty(
                        Mono.error(new RuntimeException("Transaccion Fallida Cuenta Emisora")))
              )
        );
    
  }
  
  /*
   * Obtiene la información de saldos diarios por ID de producto.
   * 
   * @param idProducto Identificador del producto del cual se desea 
   * obtener la información de saldos.
   * @return Mono que emite la respuesta con la información de saldos diarios.
   */
  @Override
  public Mono<SaldoDiarioInfoRes> getInformSaldosByIdProducto(String idProducto) {
    return servProdApi.getProductoRoles(idProducto)
        .flatMap(prodApi -> 
          mongoOperations.find(servMovRepo.getDatosDeEsteMesQuery(idProducto), 
              Transaccion.class)
              .collectList()
              .flatMap(listTransacciones -> 
                Mono.just(
                    TransaccionesUtils.getInfoSaldoDiario(idProducto, listTransacciones)
                    )
              )
        );
  }
  
  /*
   * Obtiene todos los impuestos asociados a un producto por su ID.
   * 
   * @param idProducto Identificador del producto del cual se desean 
   * obtener los impuestos.
   * @return Mono que emite la respuesta con todos los impuestos asociados al producto.
   */
  @Override
  public Mono<TransaccionRes> getAllTaxByIdProduct(String idProducto) {
    return servProdApi.getProductoRoles(idProducto)
        .flatMap(prodApi -> 
          mongoOperations.find(servMovRepo.getDatosDeEsteMesQuery(idProducto), 
              Transaccion.class)
              .filter(itemDb -> itemDb.getObservacionTransaccion().contains("COMISION"))
              .single()
              .flatMap(itemAf1 -> 
                Mono.just(ModelMapperUtils.map(itemAf1, TransaccionRes.class))
              )
        );
  }
  
  
  /*
   * 
   * rollback transaccion 
   * 
   */
  public Mono<TransaccionRes> rollBackTransaccion(InfoTransacionReq transaccion) {
    return postTransaccion(transaccion);    
  }
  
  /*
   * 
   * Buscar Saldo Actual
   */  
  private Mono<Saldo> getSaldoPorIdProd(String idProducto) {
    return servSaldoRepo.findFirstByCodigoProducto(idProducto)
    .flatMap(Mono::just);
  }


}
