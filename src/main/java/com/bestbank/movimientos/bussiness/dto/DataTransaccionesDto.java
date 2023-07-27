package com.bestbank.movimientos.bussiness.dto;

import com.bestbank.movimientos.domain.model.Saldo;
import com.bestbank.movimientos.domain.model.Transaccion;
import com.bestbank.movimientos.domain.utils.ResultadoTransaccion;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Clase que representa los datos de las transacciones.
 * Esta clase se utiliza para transferir información relacionada 
 * con las transacciones entre diferentes componentes del sistema.
 */

@AllArgsConstructor
@Data
public class DataTransaccionesDto {
  
  private Saldo nuevoSaldoReg;
  
  private ResultadoTransaccion resultadoTransaccion;
  
  private List<Transaccion> nuevasTransacciones;
}
