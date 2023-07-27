package com.bestbank.movimientos.domain.model;


import com.bestbank.movimientos.domain.utils.GrupoProducto;
import com.bestbank.movimientos.domain.utils.ResultadoTransaccion;
import com.bestbank.movimientos.domain.utils.TipoInstrumento;
import com.bestbank.movimientos.domain.utils.TipoOperacion;
import com.bestbank.movimientos.domain.utils.TipoProducto;
import java.beans.Transient;
import java.util.Calendar;
import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Representa una transacción.
 * La clase Transaccion es una entidad que se mapea a la
 *  colección "movimientos" en la base de datos.
 */
@Document(collection = "movimientos")
@Data
public class Transaccion {
  
  @Id
  private String id;
  
  private String codControl;
  
  private GrupoProducto grupoProducto;
  
  private TipoProducto tipoProducto;
  
  private String codPersona;
  
  private String codigoProducto;
  
  private TipoOperacion codigoOperacion;
  
  private Double montoTransaccion;
  
  private Date fechaTransaccion;
  
  private ResultadoTransaccion resultadoTransaccion;
  
  private String observacionTransaccion;
  
  private Double saldoInicial;
  
  private Double saldoFinal;
  
  private TipoInstrumento tipoInstrumento;
  
  private String idInstrumento;
  
  @Transient
  public int getDiaTransaccion() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(this.getFechaTransaccion());
    return calendar.get(Calendar.DAY_OF_MONTH);
  }
  

}
