package com.bestbank.movimientos.bussiness.dto.res;

import java.io.Serializable;

import com.bestbank.movimientos.domain.utils.TipoPersonaRol;

import lombok.Data;

@Data
public class PersonaRoles implements Serializable{
  
  /**
   * 
   */
  private static final long serialVersionUID = -4670435393085960694L;

  private String codigoPersona;
  
  private TipoPersonaRol rol;
  

}