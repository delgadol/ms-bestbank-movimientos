package com.bestbank.movimientos.bussiness.dto.res;

import java.util.List;

import com.bestbank.movimientos.bussiness.dto.ProductoAsociado;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class InstrumentoAsoRes extends InstrumentoRes {

  private List<ProductoAsociado> productosAsociados;
  
}
