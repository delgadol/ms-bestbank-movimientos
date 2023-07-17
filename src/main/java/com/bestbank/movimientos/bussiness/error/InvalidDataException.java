package com.bestbank.movimientos.bussiness.error;

import org.springframework.validation.BindingResult;

/**
 * Excepción que se lanza cuando se encuentran datos inválidos o incorrectos.
 * Esta clase hereda de la clase RuntimeException.
 */
public class InvalidDataException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final transient BindingResult result;

  public InvalidDataException(BindingResult result) {
    super();
    this.result = result;
  }

  public InvalidDataException(String message, BindingResult result) {
    super(message);
    this.result = result;
  }

  public BindingResult getResult() {
    return result;
  }
}