package com.bestbank.movimientos.domain.utils;

/**
 * Enumeración que representa los diferentes tipos de operaciones.
 */
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
