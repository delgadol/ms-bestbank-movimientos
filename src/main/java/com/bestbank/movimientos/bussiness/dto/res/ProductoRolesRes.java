package com.bestbank.movimientos.bussiness.dto.res;

import java.util.ArrayList;
import java.util.List;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Clase que representa la respuesta de los roles de un producto, 
 * extendiendo la clase "ProductoRes".
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductoRolesRes extends ProductoRes {
  

  private String codigoPersona;
  
  private Double costExtraOperacionesMes;
  
  private Double minSaldoMensual;
  
  private Double costMinSaldoMensual;
  
  private List<PersonaRoles> personaRoles = new ArrayList<>();
  
  
  

}
