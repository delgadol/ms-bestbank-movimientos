package com.bestbank.movimientos.bussiness.dto.req;

import com.bestbank.movimientos.domain.utils.TipoOperacion;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class InfoTransaccionInternaReq {
  
  @NotEmpty
  private String codPersona;
  
  @NotNull
  private TipoOperacion tipoOperacion;
  
  @NotEmpty
  private String desdeIdProducto;
  
  @NotEmpty
  private String paraIdProducto;
  
  
  @NotNull
  private Double montoOperacion;
  
  @NotEmpty
  private String obervacionTransaccion;
}
