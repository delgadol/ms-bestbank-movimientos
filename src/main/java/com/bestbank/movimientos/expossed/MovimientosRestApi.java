package com.bestbank.movimientos.expossed;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bestbank.movimientos.bussiness.dto.req.InfoTransacionReq;
import com.bestbank.movimientos.bussiness.dto.res.SaldoRes;
import com.bestbank.movimientos.bussiness.dto.res.TransaccionRes;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping("/v1/transacciones")
public class MovimientosRestApi {
  
  /**
   * Obtiene el saldo de un producto específico.
   *
   * @param idProducto el identificador del producto
   * @return un Mono que emite el objeto SaldoRes del producto
   */
  
  @GetMapping("/{idProducto}/saldo")
  public Mono<SaldoRes> getProductBalance(@PathVariable(name="idProducto") String idProdcuto){
    return null;
  }
  
  /**
   * Obtiene todos los saldos relacionados a un cliente dado.
   *
   * @param idCliente el identificador del cliente
   * @return un Flux que emite objetos SaldoRes
   */
  @GetMapping("/clientes/{idCliente}/resumenes")
  public Flux<SaldoRes> getAllBalanceByClientId(@PathVariable(name="idCliente") String idCliente){
    return null;
  }
  
  /**
   * Crea una nueva transacción con la información proporcionada.
   *
   * @param transaccion la información de la transacción a crear
   * @return un Mono que emite el objeto TransaccionRes resultante
   */
  @PostMapping("")
  public Mono<TransaccionRes> postTransaccion(@Valid @RequestBody InfoTransacionReq transaccion){
    return null;
  }
  
  /**
   * Obtiene todas las transacciones relacionadas a un producto dado.
   *
   * @param idProducto el identificador del producto
   * @return un Flux que emite objetos TransaccionRes
   */ 
  @GetMapping("/{idProducto}")
  public Flux<TransaccionRes> getAllTransaccionByProductID(@PathVariable(name="idProducto") String idProducto){
    return null; 
  }

}
