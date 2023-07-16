package com.bestbank.movimientos.bussiness.dto.req;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class InfoTransaccionInternaReq extends InfoTransacionReq{
  
  @NotEmpty
  private String IdProducto2;
  
}
