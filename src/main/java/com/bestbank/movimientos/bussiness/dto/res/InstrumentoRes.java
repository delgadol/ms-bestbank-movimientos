package com.bestbank.movimientos.bussiness.dto.res;

import java.util.Date;

import com.bestbank.movimientos.domain.utils.TipoInstrumento;

import lombok.Data;

/**
 * Clase que representa la respuesta de una solicitud de instrumento.
 * Contiene información y detalles relacionados con un instrumento específico.
 */

@Data
public class InstrumentoRes {

  private String id;
  
  private TipoInstrumento tipoInstrumento;
  
  private String codigoInstrumento;
  
  private String codigoPersona;
  
  private Date fecInicio;
  
  private Date fecFinal;
  
  private Date fecCreacion;
  
}
