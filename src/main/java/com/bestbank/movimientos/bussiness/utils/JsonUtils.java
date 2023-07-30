package com.bestbank.movimientos.bussiness.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class JsonUtils {

  private JsonUtils() {}
  
  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Convierte un objeto a una cadena JSON.
   *
   * @param obj El objeto que se desea convertir a JSON.
   * @return Una cadena JSON que representa el objeto.
   * @throws JsonProcessingException Si ocurre un error al procesar el objeto.
   */
  public static String objectToJson(Object obj)  {
      try {
        return objectMapper.writeValueAsString(obj);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      return "";
  }

  /**
   * Convierte una cadena JSON en un objeto Java.
   *
   * @param jsonStr La cadena JSON que se desea convertir.
   * @param clazz   La clase del objeto al que se debe convertir el JSON.
   * @param <T>     El tipo de clase.
   * @return El objeto Java generado a partir de la cadena JSON.
   * @throws IOException 
   */
  public static <T> T jsonToObject(String jsonStr, Class<T> clazz) {
    try {
      return objectMapper.readValue(jsonStr, clazz);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }       
    return null;
  }
}