package com.bestbank.movimientos.bussiness.dto.req;

import com.bestbank.movimientos.domain.utils.TipoOperacion;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Clase que representa una solicitud de información de transacción relacionada 
 * con un instrumento.
 * Contiene los datos y detalles necesarios para crear o procesar 
 * una transacción asociada a un instrumento específico.
 * 
 */

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
