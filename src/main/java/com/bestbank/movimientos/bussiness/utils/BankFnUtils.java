package com.bestbank.movimientos.bussiness.utils;

import java.time.LocalDateTime;
import java.util.UUID;

public class BankFnUtils {
  
  public static String uniqueProductCode() {
    UUID uuid = UUID.randomUUID();
    return uuid.toString();
  }
  
  public static java.sql.Timestamp getDateTime() {
    return java.sql.Timestamp.valueOf(LocalDateTime.now());
  }
  
}
