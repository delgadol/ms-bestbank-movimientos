package com.bestbank.movimientos.bussiness.dto.res;

import java.util.Date;

import com.bestbank.movimientos.domain.utils.ResultadoTransaccion;
import com.bestbank.movimientos.domain.utils.TipoOperacion;

import lombok.Data;

/**
 * Clase que representa la respuesta de un saldo.
 */
@Data
public class TransaccionRes {

  private String codControl;
  
  private TipoOperacion codigoOperacion;

  private String codigoProducto;  
  
  private Double montoTransaccion;
  
  private Date fechaTransaccion;
  
  private ResultadoTransaccion resultadoTransaccion;
  
  private String observacionTransaccion;
  
  private Double saldoFinal;
  
}
