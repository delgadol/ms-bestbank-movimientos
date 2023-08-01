package com.bestbank.movimientos.bussiness.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class InfoTransaccionBrokerDualReq extends InfoTransacionBrokerReq {
  
  private String idProducto2;

}
