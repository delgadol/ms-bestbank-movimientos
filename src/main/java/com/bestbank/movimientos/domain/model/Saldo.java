package com.bestbank.movimientos.domain.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.bestbank.movimientos.domain.utils.GrupoProducto;
import com.bestbank.movimientos.domain.utils.TipoProducto;

import lombok.Data;

/**
 * Representa el saldo de una entidad.
 * La clase Saldo es una entidad que se mapea a la colección "saldos" en la base de datos.
 */
@Document(collection = "saldos")
@Data
public class Saldo {
  
  @Id
  private String id;
  
  private String codControl;
  
  private String idPersona;
  
  private GrupoProducto grupoProdcuto;
  
  private TipoProducto tipoProducto;
  
  private String codigoProducto;
  
  private Double saldoActual;
  
  private Date fechaActualizacion;

}
