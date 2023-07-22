package com.bestbank.movimientos.bussiness.dto;

import com.bestbank.movimientos.domain.utils.GrupoProducto;
import com.bestbank.movimientos.domain.utils.TipoProducto;

import lombok.Data;

/**
 * Clase que representa un producto asociado a un instrumento en el sistema.
 * Contiene información y datos relacionados con un producto específico 
 * asociado a un instrumento.
 * 
 */

@Data
public class ProductoAsociado {
  
  private String id;
  
  private String codigoProducto;
  
  private Integer indDefecto;
  
  private TipoProducto tipoProducto;
  
  private GrupoProducto grupoProducto;


}
