package com.bestbank.movimientos.domain.repositories;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bestbank.movimientos.domain.model.Transaccion;


public interface MovimientosRepository extends ReactiveMongoRepository<Transaccion, String>{
  
  default Query getDatosDeEsteMesQuery(String codigoProducto) {
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        Date startDate = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(lastDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Criteria criteria = Criteria.where("fechaTransaccion").gte(startDate).lte(endDate)
            .and("codigoProducto").is(codigoProducto);
        return new Query(criteria);
    }
  
  default Query getDatosPorCodigoYFechaActualY3MesesAtrasQuery(String codigoProducto) {
        LocalDate fechaActual = LocalDate.now().plusDays(1);
        LocalDate fechaHasta = fechaActual.minusMonths(3);

        Date fechaActualDate = Date.from(fechaActual.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fechaHastaDate = Date.from(fechaHasta.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Criteria criteria = new Criteria();
        criteria.and("codigoProducto").is(codigoProducto)
                .and("fechaTransaccion").gte(fechaHastaDate).lte(fechaActualDate);

        return new Query(criteria);
    }

}
