package com.bestbank.movimientos.bussiness.dto.res;

import com.bestbank.movimientos.domain.utils.TipoProducto;

import lombok.Data;

@Data
public class SaldoRes {
  
  private String codControl;

  private TipoProducto tipoProducto;

  private String codigoProducto;

  private Double saldoActual;
  
}
