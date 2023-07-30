package com.bestbank.movimientos.bussiness.message.dto;


import com.bestbank.movimientos.domain.utils.TipoOperacion;
import lombok.Data;

/**
 * Clase que representa la solicitud de información de transacción.
 */

@Data
public class InfoTransacionBrokerReq {
  
  private String codCtrlBroker;

  private String idProducto;

  private String codPersona;

  private TipoOperacion tipoOperacion;
  
  private Double montoOperacion;

  private String observacionTransaccion;
  
}