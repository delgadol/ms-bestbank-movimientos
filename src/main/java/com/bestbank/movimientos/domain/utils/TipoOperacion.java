package com.bestbank.movimientos.domain.utils;

public enum TipoOperacion {
  
  ABONO(1),
  CARGO(-1),
  CONSULTA(0),
  TRFINTERNA(2);
  
  private int operacion;

  TipoOperacion(int i) {
    this.operacion = i;
  }

  public int getOperacion() {
    return operacion;
  }
  
}
