package com.bestbank.movimientos.bussiness.dto;

import com.bestbank.movimientos.domain.utils.GrupoProducto;
import com.bestbank.movimientos.domain.utils.TipoProducto;

import lombok.Data;

@Data
public class ProductoAsociado {
  
  private String id;
  
  private String codigoProducto;
  
  private Integer indDefecto;
  
  private TipoProducto tipoProducto;
  
  private GrupoProducto grupoProducto;


}
