package com.bestbank.movimientos.bussiness.utils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bestbank.movimientos.bussiness.dto.req.InfoTransacionReq;
import com.bestbank.movimientos.bussiness.dto.res.ProductoRolesRes;
import com.bestbank.movimientos.bussiness.dto.res.SaldoDiarioInfoRes;
import com.bestbank.movimientos.domain.model.Transaccion;
import com.bestbank.movimientos.domain.utils.ResultadoTransaccion;
import com.bestbank.movimientos.domain.utils.TipoOperacion;

import lombok.extern.slf4j.Slf4j;
/**
 * Clase de utilidades relacionadas con las transacciones.
 * Proporciona métodos y funciones comunes utilizados en el contexto de las transacciones.
 */
@Slf4j
public class TransaccionesUtils {
  
  
  private TransaccionesUtils() {
    
  }
  
  private static List<TipoOperacion> reqVerifCliente = Arrays.asList(TipoOperacion.CARGO);
  
  public static Boolean getRequiereVerificarCliente(TipoOperacion tipoOperacion) {
    return (reqVerifCliente.contains(tipoOperacion));
  }
  
  /**
   * Verifica si un cliente está autorizado para realizar una transacción.
   *
   * @return true si el cliente está autorizado, false en caso contrario.
   */
  public static Boolean clienteAutorizado(InfoTransacionReq transaccion,
      ProductoRolesRes producto) {
    
    log.info("Validamos Requisito Verificacion");
    if (Boolean.FALSE.equals(getRequiereVerificarCliente(transaccion.getTipoOperacion()))) {
      return true;
    }
    
    log.info("Requiere Validacion - 1 Titular Cuenta");
    if (producto.getCodigoPersona().equals(transaccion.getCodPersona())) {
      return true;
    }
    
    log.info("Requiere Validacion - 2 CoTitular / 3 Firmante");
    int existePersona = producto.getPersonaRoles()
        .stream()
        .filter(x -> x.getCodigoPersona().equals(transaccion.getCodPersona()))
        .toList()
        .size();
    
    return (existePersona > 0);
    
  }
  
  /**
   * Calcula la comisión correspondiente para una transacción basándose en 
   * el número de operaciones realizadas en el mes y el producto con sus roles asociados.
   *
   * @param numOprMes El número de operaciones realizadas en el mes.
   * @param maxOperacionesMes  Maximo d eoperciones por mes
   * @param costExtraOperacionesMes Maximo costo por acada operacion adcional
   * @return La comisión correspondiente a la transacción.
   */
  public static Double getComision(Long numOprMes, Integer maxOperacionesMes, 
      Double costExtraOperacionesMes) {
    log.info(String.format("Operaciones Mes : %d y Maximas Mes: %d", 
        numOprMes, maxOperacionesMes));
    return (numOprMes < maxOperacionesMes) ? 0.0D : 
      costExtraOperacionesMes;
  }
  
  /**
   * Obtiene el registro de una operación a partir de la información de transacción 
   * y el producto con sus roles asociados.
   *
   * @param transaccion La información de la transacción.
   * @param producto    El producto con sus roles asociados.
   * @return El registro de la operación.
   */
  public static Transaccion getRegistroOperacion(InfoTransacionReq transaccion, 
      ProductoRolesRes producto) {
    Transaccion nuevaTransaccion = new Transaccion();
    nuevaTransaccion.setCodControl(BankFnUtils.uniqueProductCode());
    nuevaTransaccion.setCodigoOperacion(transaccion.getTipoOperacion());
    nuevaTransaccion.setCodigoProducto(transaccion.getIdProducto());
    nuevaTransaccion.setFechaTransaccion(java.sql.Timestamp.valueOf(LocalDateTime.now()));
    nuevaTransaccion.setGrupoProducto(producto.getGrupoProducto());
    nuevaTransaccion.setMontoTransaccion(transaccion.getMontoOperacion());
    nuevaTransaccion.setTipoProducto(producto.getTipoProducto());
    nuevaTransaccion.setObservacionTransaccion(transaccion.getObervacionTransaccion());
    nuevaTransaccion.setResultadoTransaccion(ResultadoTransaccion.RECHAZADA);
    nuevaTransaccion.setCodPersona(transaccion.getCodPersona());
    return nuevaTransaccion;
  }
  
  /**
   * Calcula el nuevo saldo después de una operación, considerando el tipo de operación, 
   * el saldo actual, la comisión y el monto de la transacción.
   *
   * @param operacion    El tipo de operación realizada.
   * @param saldoActual  El saldo actual antes de la operación.
   * @param comision     La comisión aplicada a la operación.
   * @param monto        El monto de la transacción.
   * @return El nuevo saldo después de la operación.
   */
  public static Double nuevoSaldo(TipoOperacion operacion, Double saldoActual, 
      Double comision, Double monto) {
    Double saldoResult;
    if (operacion.equals(TipoOperacion.ABONO)) {
      saldoResult = saldoActual - comision + monto;
    } else if (operacion.equals(TipoOperacion.CARGO)) {
      saldoResult = saldoActual - comision - monto;
    } else {
      saldoResult = saldoActual;
    }
    return saldoResult;
  }
  
  
  public static SaldoDiarioInfoRes getInfoSaldoDiario(String idProducto, 
      List<Transaccion> listaTransacciones) {
    
    Map<Integer, Double> promedioDia = listaTransacciones.stream()
        .collect(Collectors.groupingBy(Transaccion::getDiaTransaccion,
            Collectors.averagingDouble(Transaccion::getSaldoFinal)));
    
    SaldoDiarioInfoRes informe = new SaldoDiarioInfoRes();
    informe.setIdProducto(idProducto);
    Calendar calendar = Calendar.getInstance();    
    informe.setMes(calendar.get(Calendar.MONTH) + 1);
    informe.setDatosInforme(promedioDia);
    
    return informe;
    
  }
  
  

}
