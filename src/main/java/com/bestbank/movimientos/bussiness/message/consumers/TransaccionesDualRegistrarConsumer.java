package com.bestbank.movimientos.bussiness.message.consumers;

import com.bestbank.movimientos.bussiness.dto.req.InfoTransaccionInternaReq;
import com.bestbank.movimientos.bussiness.message.dto.InfoTransaccionBrokerDualReq;
import com.bestbank.movimientos.bussiness.message.dto.TransaccionBrokerRes;
import com.bestbank.movimientos.bussiness.message.producers.TransaccionesNotificarProducer;
import com.bestbank.movimientos.bussiness.services.MovimientosService;
import com.bestbank.movimientos.bussiness.utils.JsonUtils;
import com.bestbank.movimientos.bussiness.utils.ModelMapperUtils;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransaccionesDualRegistrarConsumer {
  
  @Autowired
  private MovimientosService servMov;
  
  @Autowired
  private TransaccionesNotificarProducer servTranNot;
  
  @KafkaListener(topics = "transacciones-dual-registrar", groupId = "group_id")
  public void recibeTransaccionesRegistrar(String jsonInfoTransaccionBrokerDualReq) {
    log.info("Recibiendo Transaccion Dual para procesar");
    InfoTransaccionBrokerDualReq infoTransacionBrokerDualReq = 
        JsonUtils.jsonToObject(jsonInfoTransaccionBrokerDualReq, 
            InfoTransaccionBrokerDualReq.class);
    if (Objects.isNull(infoTransacionBrokerDualReq)) {
      log.error("Transaccion es Nula");
    }
    InfoTransaccionInternaReq transaccion = ModelMapperUtils.map(
        infoTransacionBrokerDualReq, InfoTransaccionInternaReq.class);    
    servMov.postTransaccionIntoBanck(transaccion)
    .subscribe(transInfo -> {
      TransaccionBrokerRes transRes = ModelMapperUtils.map(
          transInfo, TransaccionBrokerRes.class);
      transRes.setCodCtrlBroker(infoTransacionBrokerDualReq.getCodCtrlBroker());
      log.info("Enviando Respuesta de Transaccion");
      servTranNot.enviarTransaccionesNotificar(transRes);
    });
  }

}
