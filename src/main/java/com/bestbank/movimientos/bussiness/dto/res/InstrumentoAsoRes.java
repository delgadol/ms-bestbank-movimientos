package com.bestbank.movimientos.bussiness.dto.res;

import java.util.List;

import com.bestbank.movimientos.bussiness.dto.ProductoAsociado;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Clase que representa la respuesta de una solicitud de instrumento asociado.
 * Esta clase hereda de la clase InstrumentoRes y contiene información adicional
 * relacionada con la asociación de un instrumento con otros elementos del sistema.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class InstrumentoAsoRes extends InstrumentoRes {
  
  private String codPersona;

  private List<ProductoAsociado> productosAsociados;
  
}
