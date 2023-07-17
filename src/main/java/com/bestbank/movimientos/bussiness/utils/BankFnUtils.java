package com.bestbank.movimientos.bussiness.utils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase de utilidades relacionadas con funciones bancarias.
 * Proporciona métodos y funciones comunes utilizados en el contexto bancario.
 */
public class BankFnUtils {
  
  public static String uniqueProductCode() {
    UUID uuid = UUID.randomUUID();
    return uuid.toString();
  }
  
  public static java.sql.Timestamp getDateTime() {
    return java.sql.Timestamp.valueOf(LocalDateTime.now());
  }
  
}
