package com.bestbank.movimientos.bussiness.message.producers;


import com.bestbank.movimientos.bussiness.message.dto.TransaccionBrokerRes;
import com.bestbank.movimientos.bussiness.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransaccionesNotificarProducer {
  
  private static final String KAFKA_TOPIC = "transacciones-notificar";
  
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  
  public void enviarTransaccionesNotificar(TransaccionBrokerRes transaccion) {
    String jsonTransaccionBrokerRes  = JsonUtils.objectToJson(transaccion);
    log.info("cola >>" + jsonTransaccionBrokerRes );
    this.kafkaTemplate.send(KAFKA_TOPIC, jsonTransaccionBrokerRes);
    
  }

}
