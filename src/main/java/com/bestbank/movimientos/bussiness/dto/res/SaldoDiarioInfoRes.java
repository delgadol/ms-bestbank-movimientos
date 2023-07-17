package com.bestbank.movimientos.bussiness.dto.res;

import java.util.Map;

import lombok.Data;

/*
 * La clase SaldoDiarioRes representa el resultado diario del saldo de una cuenta.
 * Contiene información sobre el saldo inicial, los movimientos realizados durante el día
 *
**/
@Data
public class SaldoDiarioInfoRes {
  
  private String idProducto;
  
  private Integer mes;
  
  private Map<Integer, Double> datosInforme;

}
