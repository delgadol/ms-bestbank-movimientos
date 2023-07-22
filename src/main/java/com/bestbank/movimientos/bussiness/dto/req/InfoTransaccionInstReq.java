package com.bestbank.movimientos.bussiness.dto.req;

import com.bestbank.movimientos.domain.utils.TipoOperacion;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InfoTransaccionInstReq {
  @NotEmpty
  private String idInstrumento;
  
  @NotNull
  private TipoOperacion tipoOperacion;
  
  @NotNull
  private Double montoOperacion;
  
  @NotEmpty
  private String obervacionTransaccion;

}
