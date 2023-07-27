package com.bestbank.movimientos.bussiness.dto;

import com.bestbank.movimientos.domain.utils.GrupoProducto;
import com.bestbank.movimientos.domain.utils.TipoProducto;
import java.util.Date;
import lombok.Data;

/**
 * Clase que representa el saldo y el contador movimientos en el sistema.
 * Esta clase contiene información y datos relacionados con el saldo 
 * y movimiento de una cuenta que ha experimentado movimiento.
 * 
 */

@Data
public class SaldoContMovimientos {
  
  private String id;
  
  private String codControl;
  
  private String idPersona;
  
  private GrupoProducto grupoProdcuto;
  
  private TipoProducto tipoProducto;
  
  private String codigoProducto;
  
  private Double saldoActual;
  
  private Date fechaActualizacion;
  
  private Long contMovimientos;
  
  private Integer maxOperacionesMes; 
  
  private Double costExtraOperacionesMes;

}
