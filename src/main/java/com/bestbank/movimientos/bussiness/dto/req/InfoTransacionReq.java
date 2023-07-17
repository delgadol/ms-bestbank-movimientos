package com.bestbank.movimientos.bussiness.dto.req;

import com.bestbank.movimientos.domain.utils.TipoOperacion;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Clase que representa la solicitud de información de transacción.
 */

@Data
public class InfoTransacionReq {
  @NotEmpty
  private String idProducto;
  
  @NotEmpty
  private String codPersona;
  
  @NotNull
  private TipoOperacion tipoOperacion;
  
  @NotNull
  private Double montoOperacion;
  
  @NotEmpty
  private String obervacionTransaccion;
}
