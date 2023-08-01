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
    String[] idCtrlChannel = transaccion.getCodCtrlBroker().split(":");
    String kafkaChannel = KAFKA_TOPIC;
    if (idCtrlChannel.length > 1) {
      transaccion.setCodCtrlBroker(idCtrlChannel[0]);
      kafkaChannel = idCtrlChannel[1];
    }
    final String kafkaTopic = kafkaChannel;
    String jsonTransaccionBrokerRes  = JsonUtils.objectToJson(transaccion);
    log.info("cola >>" + jsonTransaccionBrokerRes );
    this.kafkaTemplate.send(kafkaTopic, jsonTransaccionBrokerRes);
    
  }

}
