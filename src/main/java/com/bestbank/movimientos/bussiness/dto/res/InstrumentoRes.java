package com.bestbank.movimientos.bussiness.dto.res;

import java.util.Date;

import com.bestbank.movimientos.domain.utils.TipoInstrumento;

import lombok.Data;

@Data
public class InstrumentoRes {

  private String id;
  
  private TipoInstrumento tipoInstrumento;
  
  private String codigoInstrumento;
  
  private Date fecInicio;
  
  private Date fecFinal;
  
  private Date fecCreacion;
  
}
