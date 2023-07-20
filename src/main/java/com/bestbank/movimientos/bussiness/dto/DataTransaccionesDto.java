package com.bestbank.movimientos.bussiness.dto;

import java.util.List;

import com.bestbank.movimientos.domain.model.Saldo;
import com.bestbank.movimientos.domain.model.Transaccion;
import com.bestbank.movimientos.domain.utils.ResultadoTransaccion;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DataTransaccionesDto {
  
  private Saldo nuevoSaldoReg;
  
  private ResultadoTransaccion resultadoTransaccion;
  
  private List<Transaccion> nuevasTransacciones;
}
