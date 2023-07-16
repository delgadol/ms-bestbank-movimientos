package com.bestbank.movimientos.bussiness.error;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
  
  private int status;
  
  private String message;
  
  private Date timestamp;
  
  List<String> errors;
  
  ErrorResponse(String message) {
    this.message = message;
  }

}