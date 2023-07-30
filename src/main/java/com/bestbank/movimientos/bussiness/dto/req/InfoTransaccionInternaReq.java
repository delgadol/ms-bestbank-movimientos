package com.bestbank.movimientos.bussiness.dto.req;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Clase que representa la solicitud de información de transacción interna, 
 * extendiendo la clase "InfoTransacionRe".
 * 
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class InfoTransaccionInternaReq extends InfoTransacionReq{
  
  @NotEmpty
  private String idProducto2;
  
}
