package com.bestbank.movimientos.bussiness.dto.res;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Clase que representa la respuesta de asignación de personas y roles a un producto, 
 * y extiende la clase "ProductoRes".
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductoRolesRes extends ProductoRes {
  

  
  private Double minSaldoMensual;
  
  private Double costMinSaldoMensual;
  
  private List<PersonaRoles> personaRoles;  
  
  

}
