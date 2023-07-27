package com.bestbank.movimientos.domain.repositories;

import com.bestbank.movimientos.domain.model.Transaccion;
import com.bestbank.movimientos.domain.utils.TipoInstrumento;
import com.bestbank.movimientos.domain.utils.TipoProducto;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Interfaz que define un repositorio de movimientos.
 * Proporciona métodos para acceder y gestionar los movimientos en una base de datos reactiva.
 * Esta interfaz extiende ReactiveMongoRepository y trabaja con entidades de tipo Transaccion.
 */
public interface MovimientosRepository extends ReactiveMongoRepository<Transaccion, String> {
  
  Flux<Transaccion> findAllByTipoInstrumentoAndIdInstrumentoOrderByFechaTransaccionDesc(
      TipoInstrumento tipoInstrumento, String idInstrumento);
  
  Flux<Transaccion> findAllByTipoProductoAndCodigoProductoOrderByFechaTransaccionDesc(
      TipoProducto tipoInstrumento, String codigoProducto);
  
  /**
   * Obtiene una consulta Query para buscar los datos del mes actual relacionados 
   * con un producto específico.
   *
   * @param codigoProducto El código del producto.
   * @return Una consulta Query con los criterios de búsqueda.
   */
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
  
  /**
   * Obtiene una consulta Query para buscar los datos por el código del producto y 
   * un rango de fechas desde la fecha actual hasta 3 meses atrás.
   *
   * @param codigoProducto El código del producto.
   * @return Una consulta Query con los criterios de búsqueda.
   */

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
