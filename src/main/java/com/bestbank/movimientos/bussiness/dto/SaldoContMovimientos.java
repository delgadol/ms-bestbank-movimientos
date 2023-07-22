package com.bestbank.movimientos.bussiness.dto;

import java.util.Date;

import com.bestbank.movimientos.domain.utils.GrupoProducto;
import com.bestbank.movimientos.domain.utils.TipoProducto;

import lombok.Data;

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
