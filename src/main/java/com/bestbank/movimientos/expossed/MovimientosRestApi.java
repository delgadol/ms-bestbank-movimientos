package com.bestbank.movimientos.expossed;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bestbank.movimientos.bussiness.dto.req.InfoTransaccionInstReq;
import com.bestbank.movimientos.bussiness.dto.req.InfoTransaccionInternaReq;
import com.bestbank.movimientos.bussiness.dto.req.InfoTransacionReq;
import com.bestbank.movimientos.bussiness.dto.res.SaldoDiarioInfoRes;
import com.bestbank.movimientos.bussiness.dto.res.SaldoRes;
import com.bestbank.movimientos.bussiness.dto.res.TransaccionRes;
import com.bestbank.movimientos.bussiness.services.MovimientosService;
import com.bestbank.movimientos.domain.utils.TipoInstrumento;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping("/v1/transacciones")
public class MovimientosRestApi {
  
  public MovimientosRestApi(MovimientosService servMovimientos) {
    super();
    this.servMovimientos = servMovimientos;
  }

  private final MovimientosService servMovimientos;
  
  
  
  /**
   * Obtiene el saldo de un producto específico.
   *
   * @param idProducto el identificador del producto
   * @return un Mono que emite el objeto SaldoRes del producto
   */
  
  @GetMapping("/{idProducto}/saldo")
  public Mono<SaldoRes> getProductBalance(
      @PathVariable(name = "idProducto") String idProducto) {
    return servMovimientos.getProductBalance(idProducto);
  }
  
  /**
   * Obtiene todos los saldos relacionados a un cliente dado.
   *
   * @param idCliente el identificador del cliente
   * @return un Flux que emite objetos SaldoRes
   */
  @GetMapping("/clientes/{idCliente}/resumenes")
  public Flux<SaldoRes> getAllBalanceByClientId(
      @PathVariable(name = "idCliente") String idCliente) {
    return servMovimientos.getAllBalanceByClientId(idCliente);
  }
  
  /**
   * Crea una nueva transacción con la información proporcionada.
   *
   * @param transaccion la información de la transacción a crear
   * @return un Mono que emite el objeto TransaccionRes resultante
   */
  @PostMapping("")
  public Mono<TransaccionRes> postTransaccion(@Valid @RequestBody InfoTransacionReq transaccion) {
    return servMovimientos.postTransaccion(transaccion, TipoInstrumento.CANAL_POR_DEFECTO, "");
  }
  
  /**
   * Crea una nueva transacción relacionada con un instrumento en el sistema a 
   * partir de los datos proporcionados en la solicitud.
   *
   * @param transaccion El objeto InfoTransaccionInstReq que contiene los 
   * datos de la nueva transacción a crear.
   * @return Un Mono que representa la respuesta de la solicitud (TransaccionRes).
   */

  @PostMapping("/instrumentos")
  public Mono<TransaccionRes> postTransaccionByInstrumento(
      @Valid @RequestBody InfoTransaccionInstReq transaccion) {
    return servMovimientos.postTransaccionByInstrumento(transaccion);
  }
  
  
  /**
   * Crea una nueva transacción con la información proporcionada.
   *
   * @param transaccion la información de la transacción a crear
   * @return un Mono que emite el objeto TransaccionRes resultante
   */
  @PostMapping("/internas")
  public Mono<TransaccionRes> postTransaccionInterna(
      @Valid @RequestBody InfoTransaccionInternaReq transaccion) {
    return servMovimientos.postTransaccionIntoBanck(transaccion);
  }
  
  /**
   * Obtiene todas las transacciones relacionadas a un producto dado.
   *
   * @param idProducto el identificador del producto
   * @return un Flux que emite objetos TransaccionRes
   */ 
  @GetMapping("/{idProducto}")
  public Flux<TransaccionRes> getAllTransaccionByProductId(
      @PathVariable(name = "idProducto") String idProducto) {
    return servMovimientos.getAllTransaccionByProductId(idProducto); 
  }
  
  /* Obtiene los saldos promedios por dias de registro
   *
   * @param idProducto el identificador del producto
   * @return un Monoque emite objetos SaldoDiarioInfoRes
   */ 
  @GetMapping("/{idProducto}/saldosdiarios")
  public Mono<SaldoDiarioInfoRes> getInformSaldosByIdProducto(
      @PathVariable(name = "idProducto") String idProducto) {
    return servMovimientos.getInformSaldosByIdProducto(idProducto);    
  }
  
    /* Obtiene los comisiones cobradas
    *
    * @param idProducto el identificador del producto
    * @return un Mono que emite objetos SaldoDiarioInfoRes
    */ 
  @GetMapping("/{idProducto}/comisiones")
  public Mono<TransaccionRes> getAllTaxByIdProduct(
      @PathVariable(name = "idProducto") String idProducto) {
    return servMovimientos.getAllTaxByIdProduct(idProducto);
  }
  
  /**
   * Obtiene todas las transacciones relacionadas con un instrumento específico en
   *  el sistema.
   *
   * @param idInstrumento El identificador único del instrumento del que se desean 
   * obtener las transacciones.
   * @return Un Flux que representa la secuencia de respuestas de las solicitudes 
   * (TransaccionRes).
   */

  @GetMapping("/instrumentos/{idInstrumento}/recientes")
  public Flux<TransaccionRes> getAllTransaccionByInstrument(
      @PathVariable(name = "idInstrumento") String idInstrumento) {
    return servMovimientos.getAllTransaccionByInstrument(idInstrumento);
  }
  
  /**
   * Obtiene el saldo del producto asociado a un instrumento específico en el sistema.
   *
   * @param idInstrumento El identificador único del instrumento del que se desea
   *  obtener el saldo del producto asociado.
   * @return Un Mono que representa la respuesta de la solicitud (SaldoRes).
   */

  @GetMapping("/instrumentos/{idInstrumento}/saldos")
  public Mono<SaldoRes> getProductBalanceByInstrument(
      @PathVariable(name = "idInstrumento") String idInstrumento) {
    return servMovimientos.getProductBalanceByInstrument(idInstrumento);
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
  @GetMapping("/productos/{idProducto}/recientes")
  public Flux<TransaccionRes> getAllLastTraccionByProductId(
      @PathVariable(name = "idProducto")  String idProducto) {
    return servMovimientos.getAllLastTraccionByProductId(idProducto);
    
  }
  

}
