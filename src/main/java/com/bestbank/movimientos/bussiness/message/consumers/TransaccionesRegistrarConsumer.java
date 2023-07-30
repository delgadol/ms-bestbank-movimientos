package com.bestbank.movimientos.bussiness.message.consumers;

import com.bestbank.movimientos.bussiness.dto.req.InfoTransacionReq;
import com.bestbank.movimientos.bussiness.message.dto.InfoTransacionBrokerReq;
import com.bestbank.movimientos.bussiness.message.dto.TransaccionBrokerRes;
import com.bestbank.movimientos.bussiness.message.producers.TransaccionesNotificarProducer;
import com.bestbank.movimientos.bussiness.services.MovimientosService;
import com.bestbank.movimientos.bussiness.utils.JsonUtils;
import com.bestbank.movimientos.bussiness.utils.ModelMapperUtils;
import com.bestbank.movimientos.domain.utils.TipoInstrumento;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransaccionesRegistrarConsumer {
  
  @Autowired
  private MovimientosService servMov;
  
  @Autowired
  private TransaccionesNotificarProducer servTranNot;
  
  @KafkaListener(topics = "transacciones-registrar", groupId = "group_id")
  public void recibeTransaccionesRegistrar(String jsonInfoTransaccionBrokerReq) {
    log.info("Recibiendo Transaccion para procesar");
    InfoTransacionBrokerReq infoTransacionBrokerReq = 
        JsonUtils.jsonToObject(jsonInfoTransaccionBrokerReq, InfoTransacionBrokerReq.class);
    if (Objects.isNull(infoTransacionBrokerReq)) {
      log.error("Transaccion es Nula");
    }
    InfoTransacionReq transaccion = ModelMapperUtils.map(
        infoTransacionBrokerReq, InfoTransacionReq.class);    
    servMov.postTransaccion(transaccion, TipoInstrumento.CANAL_POR_DEFECTO, "")
    .subscribe(transInfo -> {
      TransaccionBrokerRes transRes = ModelMapperUtils.map(
          transInfo, TransaccionBrokerRes.class);
      transRes.setCodCtrlBroker(infoTransacionBrokerReq.getCodCtrlBroker());
      log.info("Enviando Respuesta de Transaccion");
      servTranNot.enviarTransaccionesNotificar(transRes);
    });
  }

}
