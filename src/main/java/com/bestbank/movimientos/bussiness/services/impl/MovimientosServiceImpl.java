package com.bestbank.movimientos.bussiness.services.impl;

import com.bestbank.movimientos.bussiness.dto.DataTransaccionesDto;
import com.bestbank.movimientos.bussiness.dto.ProductoAsociado;
import com.bestbank.movimientos.bussiness.dto.SaldoContMovimientos;
import com.bestbank.movimientos.bussiness.dto.req.InfoTransaccionInstReq;
import com.bestbank.movimientos.bussiness.dto.req.InfoTransaccionInternaReq;
import com.bestbank.movimientos.bussiness.dto.req.InfoTransacionReq;
import com.bestbank.movimientos.bussiness.dto.res.InstrumentoAsoRes;
import com.bestbank.movimientos.bussiness.dto.res.ProductoRolesRes;
import com.bestbank.movimientos.bussiness.dto.res.SaldoDiarioInfoRes;
import com.bestbank.movimientos.bussiness.dto.res.SaldoRes;
import com.bestbank.movimientos.bussiness.dto.res.TransaccionRes;
import com.bestbank.movimientos.bussiness.services.InstrumentosApiClientService;
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
import java.util.ArrayList;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Transactional
public class MovimientosServiceImpl implements MovimientosService {

  private final ProductoApiClientService servProdApi;
  
  private final InstrumentosApiClientService servInstApi;
  
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
      ReactiveMongoOperations mongoOperations, 
      InstrumentosApiClientService servInstapi) {
    super();
    this.servProdApi = api;
    this.servMovRepo = servMovRepo;
    this.servSaldoRepo = servRepoSaldo;
    this.mongoOperations = mongoOperations;
    this.servInstApi = servInstapi;
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
  
  /**
   * Verifica si una cuenta con saldo y movimientos soporta la operación de acuerdo 
   * a la información de la transacción.
   *
   * @param saldoCuenta El objeto SaldoContMovimientos que representa el saldo 
   * contable con movimientos de la cuenta.
   * @param transaccion El objeto InfoTransaccionInstReq que contiene la información 
   * de la transacción a realizar.
   * @return true si la cuenta con saldo y movimientos soporta la operación, 
   * false en caso contrario.
   */

  private Boolean ctaSoportaOperacion(SaldoContMovimientos saldoCuenta,
      InfoTransaccionInstReq transaccion) {
    Double comision = TransaccionesUtils.getComision(saldoCuenta.getContMovimientos(), 
        saldoCuenta.getMaxOperacionesMes(), saldoCuenta.getCostExtraOperacionesMes());
    Double nuevoSaldo = TransaccionesUtils.nuevoSaldo(
        transaccion.getTipoOperacion(), saldoCuenta.getSaldoActual(), 
        comision, transaccion.getMontoOperacion());
    return (saldoCuenta.getSaldoActual() >= (nuevoSaldo + comision));
  }
  
  /**
   * Encuentra el producto predeterminado para realizar una transacción a partir 
   * de la lista de productos asociados.
   *
   * @param prodAsociados El Flux que representa la lista de productos asociados al 
   * instrumento.
   * @return Un Mono que contiene el identificador del producto predeterminado para 
   * la transacción.
   */

  private Mono<String> productoDefTransaccion(Flux<ProductoAsociado> prodAsociados) {
    return prodAsociados
        .flatMap(producto -> 
          Mono.just(producto.getId())
        )
        .take(1)
        .single();
  }
  
  /**
   * Obtiene la información de la transacción relacionada con un producto e instrumento 
   * específico.
   *
   * @param transaccion El objeto InfoTransaccionInstReq que contiene los datos de la 
   * transacción a obtener.
   * @return Un Mono que representa la información de la transacción requerida 
   * (InfoTransacionReq).
   */
  private Mono<InfoTransacionReq> getProductoInstTransaccion(InfoTransaccionInstReq transaccion) {
  
    Mono<InstrumentoAsoRes> instApiData = 
        servInstApi.getInstrumentoInfo(transaccion.getIdInstrumento());
    
    Flux<ProductoAsociado> prodAsociados = instApiData
        .flux()
        .flatMap(instInfo -> 
          Flux.fromIterable(instInfo.getProductosAsociados())
        )
        .switchIfEmpty(
          Mono.error(new NoSuchMethodError("Instrumento no tiene Asociados"))
        );
     
    Flux<SaldoContMovimientos> saldoMovientos = prodAsociados
        .flatMap(prodAsoc -> 
          servProdApi.getProducto(prodAsoc.getId())
              .flux()
              .flatMap(prodInfo -> 
                servSaldoRepo.findFirstByCodigoProducto(prodInfo.getId())
                  .flatMap(saldoCta -> {
                    SaldoContMovimientos saldoMovProd = ModelMapperUtils.map(saldoCta, 
                        SaldoContMovimientos.class);
                    saldoMovProd.setMaxOperacionesMes(prodInfo.getMaxOperacionesMes());
                    saldoMovProd.setCostExtraOperacionesMes(prodInfo.getCostExtraOperacionesMes());
                    return Mono.just(saldoMovProd);
                  })
              )
        );

    Flux<SaldoContMovimientos> saldoMovientosOptMes = saldoMovientos
        .flatMap(saldoCuenta -> 
          mongoOperations.count(
              servMovRepo.getDatosDeEsteMesQuery(saldoCuenta.getCodigoProducto()), 
              Transaccion.class)
              .flatMap(contOptMes -> {
                saldoCuenta.setContMovimientos(contOptMes);
                return Mono.just(saldoCuenta);
              })
        );
    
    Mono<String> prodTransaccion = saldoMovientosOptMes
        .filter(saldProd -> ctaSoportaOperacion(saldProd, transaccion))
        .take(1)
        .single()
        .map(SaldoContMovimientos::getCodigoProducto)
        .switchIfEmpty(productoDefTransaccion(prodAsociados));

    prodTransaccion.subscribe(t -> log.info("PRDUCTO " + t));
    
   return instApiData
      .flatMap(instInfo -> 
        prodTransaccion
            .flatMap(prodPayCode -> 
             servProdApi.getProducto(prodPayCode)
                  .flatMap(prodInfo -> {
                    InfoTransacionReq nuevaTransaccion = 
                        ModelMapperUtils.map(transaccion, InfoTransacionReq.class);
                    nuevaTransaccion.setCodPersona(prodInfo.getCodigoPersona());
                    nuevaTransaccion.setIdProducto(prodInfo.getId());
                    return Mono.just(nuevaTransaccion);
                  })
            )
      );
    
  }
  
  /**
   * Obtiene los datos necesarios para realizar una transacción a partir 
   * de la información proporcionada.
   *
   * @param prodRolApi El objeto ProductoRolesRes que representa los roles 
   * del producto asociado a la transacción.
   * @param numOptmes El número de opción del mes para la transacción.
   * @param saldoActual El objeto Saldo que representa el saldo actual de la cuenta.
   * @param transaccion El objeto InfoTransacionReq que contiene los datos de la transacción.
   * @param tipoInstrumento El TipoInstrumento que indica el tipo de instrumento 
   * relacionado con la transacción.
   * @param idInstrumento El identificador del instrumento relacionado con la transacción.
   * @return Un objeto DataTransaccionesDto que contiene los datos requeridos para 
   * realizar la transacción.
   */

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
    List<Transaccion> listaTransacciones = new ArrayList<>();   
    Double comision = TransaccionesUtils.getComision(numOptmes, 
        prodRolApi.getMaxOperacionesMes(), prodRolApi.getCostExtraOperacionesMes());
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
  
  /**
   * Crea una nueva transacción relacionada con un instrumento en el sistema a partir
   *  de los datos proporcionados en la solicitud.
   *
   * @param transaccion El objeto InfoTransaccionInstReq que contiene los datos de la 
   * nueva transacción a crear.
   * @return Un Mono que representa la respuesta de la solicitud (TransaccionRes).
   */
  @Override
  public Mono<TransaccionRes> postTransaccionByInstrumento(
      InfoTransaccionInstReq transaccion) {
    return getProductoInstTransaccion(transaccion)
        .flatMap(prodTransaccion -> {
          InfoTransacionReq operacion = ModelMapperUtils.map(transaccion, InfoTransacionReq.class);
          operacion.setCodPersona(prodTransaccion.getCodPersona());
          operacion.setIdProducto(prodTransaccion.getIdProducto());
          return postTransaccion(operacion, 
              TipoInstrumento.TARJETA_DEBITO, transaccion.getIdInstrumento());
        });
  }
  
  
  /*
   * Crea una nueva transacción y la guarda en el sistema.
   * 
   * @param transaccion Objeto que contiene la información de la transacción a crear.
   * @return Mono que emite la respuesta de la transacción creada.
   */  
  @Override
  public Mono<TransaccionRes> postTransaccion(InfoTransacionReq transaccion,
      TipoInstrumento tipoInstrumento, String idInstrumento) {
    return servProdApi.getProductoRoles(transaccion.getIdProducto())
        .filter(prodRolApiF1 -> TransaccionesUtils.clienteAutorizado(transaccion, prodRolApiF1))
        .switchIfEmpty(Mono.error(new Throwable("Cliente no Autorizado")))
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
                        tipoInstrumento, idInstrumento);
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
  
  /**
   * Realiza una solicitud POST para realizar una transacción interna en el banco.
   *
   * @param operacionInterna La información de transacción interna.
   * @return Un Mono que emite la respuesta de la transacción realizada.
   */
  @Override
  public Mono<TransaccionRes> postTransaccionIntoBanck(InfoTransaccionInternaReq operacionInterna) {
    
    if (operacionInterna.getIdProducto().contains(operacionInterna.getIdProducto2())) {
      throw new DuplicateFormatFlagsException("Productos deben ser diferentes");
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
                postTransaccion(outTransaccion, TipoInstrumento.CANAL_POR_DEFECTO, "")
                    .filter(outTransRes -> 
                    outTransRes.getResultadoTransaccion() == ResultadoTransaccion.APROBADA)
                    .flatMap(transOut -> 
                      postTransaccion(inTransaccion,  TipoInstrumento.CANAL_POR_DEFECTO, "")
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
    return postTransaccion(transaccion,  TipoInstrumento.CANAL_POR_DEFECTO, "");    
  }
  
  /*
   * Buscar Saldo Actual
   */  
  private Mono<Saldo> getSaldoPorIdProd(String idProducto) {
    return servSaldoRepo.findFirstByCodigoProducto(idProducto)
    .flatMap(Mono::just);
  }

  /**
   * Obtiene el saldo del producto asociado a un instrumento específico en el sistema.
   *
   * @param idInstrumento El identificador único del instrumento del que se desea 
   * obtener el saldo del producto asociado.
   * @return Un Mono que representa la respuesta de la solicitud (SaldoRes).
   */

  @Override
  public Mono<SaldoRes> getProductBalanceByInstrument(String idInstrumento) {
    //Mono<String> idCtaPorDefecto = 
    return  servInstApi.getInstrumentoInfo(idInstrumento)
        .map(instData ->  
          (!instData.getProductosAsociados().isEmpty()) 
          ? instData.getProductosAsociados().get(0).getId() : null
        )
        .flatMap(this::getProductBalance);
  }

  /**
   * Obtiene todas las transacciones relacionadas con un instrumento específico en 
   * el sistema.
   *
   * @param idInstrumento El identificador único del instrumento del que se desean 
   * obtener las transacciones.
   * @return Un Flux que representa la secuencia de respuestas de las solicitudes 
   * (TransaccionRes).
    */
  @Override
  public Flux<TransaccionRes> getAllTransaccionByInstrument(String idInstrumento) {
    return servInstApi.getInstrumentoInfo(idInstrumento)
        .flux()
        .flatMap(instInfo -> 
          servMovRepo
          .findAllByTipoInstrumentoAndIdInstrumentoOrderByFechaTransaccionDesc(
              instInfo.getTipoInstrumento(), idInstrumento)
              .map(s -> ModelMapperUtils.map(s, TransaccionRes.class))
        )
        .take(10);
  }

  /**
   * Obtiene todas las últimas transacciones relacionadas con un producto específico 
   * en el sistema.
   *
   * @param idProducto El identificador único del producto del que se desean obtener 
   * las últimas transacciones.
   * @return Un Flux que representa la secuencia de respuestas de las solicitudes 
   * (TransaccionRes).
   */
  @Override
  public Flux<TransaccionRes> getAllLastTraccionByProductId(String idProducto) {
    return servProdApi.getProducto(idProducto)
        .flux()
        .flatMap(prodInf -> 
          servMovRepo
          .findAllByTipoProductoAndCodigoProductoOrderByFechaTransaccionDesc(
              prodInf.getTipoProducto(), prodInf.getId())
          .map(s -> ModelMapperUtils.map(s, TransaccionRes.class))
        )
        .take(10);
  }
  
  


}
