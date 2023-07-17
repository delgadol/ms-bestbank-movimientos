package com.bestbank.movimientos.domain.model;

import com.bestbank.movimientos.domain.utils.GrupoProducto;
import com.bestbank.movimientos.domain.utils.TipoProducto;

import lombok.Data;
/**
 * Clase que representa un producto.
 * 
 */
@Data
public class Producto {
  
  private String id;
  
  private GrupoProducto grupoProducto;
  
  private TipoProducto tipoProducto;
  
  private String codigoProducto;
  
  private String estado;
  
  private String tipoCliente;
  
  private Integer maxOperacionesMes;
  
  private Integer minDiaMesOperacion;

}
