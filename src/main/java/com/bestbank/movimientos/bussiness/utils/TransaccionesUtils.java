package com.bestbank.movimientos.bussiness.utils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.bestbank.movimientos.bussiness.dto.req.InfoTransacionReq;
import com.bestbank.movimientos.bussiness.dto.res.ProductoRolesRes;
import com.bestbank.movimientos.domain.model.Transaccion;
import com.bestbank.movimientos.domain.utils.ResultadoTransaccion;
import com.bestbank.movimientos.domain.utils.TipoOperacion;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransaccionesUtils {
  
  
  private TransaccionesUtils() {
    
  }
  
  private static List<TipoOperacion> reqVerifCliente = Arrays.asList(TipoOperacion.CARGO);
  
  public static Boolean getRequiereVerificarCliente(TipoOperacion tipoOperacion) {
    return (reqVerifCliente.contains(tipoOperacion));
  }
  
  
  public static Boolean clienteAutorizado(InfoTransacionReq transaccion ,
      ProductoRolesRes producto) {
    
    log.info("Validamos Requisito Verificacion");
    if(Boolean.FALSE.equals(getRequiereVerificarCliente(transaccion.getTipoOperacion()))) {
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
    
    return (existePersona>0);
    
  }
  
  public static Double getComision(Long numOprMes, ProductoRolesRes producto) {
    log.info(String.format("Operaciones Mes : %d y Maximas Mes: %d", numOprMes , producto.getMaxOperacionesMes()));
    return (numOprMes < producto.getMaxOperacionesMes())? 0.0D : producto.getCostExtraOperacionesMes();
  }
  
  public static Transaccion getRegistroOperacion(InfoTransacionReq transaccion, ProductoRolesRes producto) {
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
    return nuevaTransaccion;
  }
  
  public static Double nuevoSaldo(TipoOperacion operacion, Double saldoActual, Double comision, Double monto) {
    Double saldoResult = 0.00D;
    if(operacion.equals(TipoOperacion.ABONO)) {
      saldoResult = saldoActual - comision + monto;
    } else if (operacion.equals(TipoOperacion.CARGO)) {
      saldoResult = saldoActual - comision - monto;
    }else {
      saldoResult = saldoActual;
    }
    return saldoResult;
  }
  

}
