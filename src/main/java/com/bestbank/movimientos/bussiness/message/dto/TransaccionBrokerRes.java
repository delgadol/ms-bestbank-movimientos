package com.bestbank.movimientos.bussiness.message.dto;

import com.bestbank.movimientos.domain.utils.ResultadoTransaccion;
import com.bestbank.movimientos.domain.utils.TipoOperacion;
import java.util.Date;
import lombok.Data;

/**
 * Clase que representa la respuesta de un saldo.
 */
@Data
public class TransaccionBrokerRes {
  
  private String codCtrlBroker;

  private String codControl;
  
  private TipoOperacion codigoOperacion;

  private String codigoProducto;  
  
  private Double montoTransaccion;
  
  private Date fechaTransaccion;
  
  private ResultadoTransaccion resultadoTransaccion;
  
  private String observacionTransaccion;
  
  private Double saldoFinal;
  
}
