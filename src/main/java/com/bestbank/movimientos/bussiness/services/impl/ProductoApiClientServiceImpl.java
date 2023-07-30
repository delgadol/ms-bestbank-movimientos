package com.bestbank.movimientos.bussiness.services.impl;

import com.bestbank.movimientos.bussiness.client.WebClientApi;
import com.bestbank.movimientos.bussiness.dto.res.ClienteRes;
import com.bestbank.movimientos.bussiness.dto.res.ProductoRes;
import com.bestbank.movimientos.bussiness.dto.res.ProductoRolesRes;
import com.bestbank.movimientos.bussiness.services.ProductoApiClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementación de la interfaz ProductoApiClientService que proporciona 
 * servicios para interactuar con el API de clientes de productos.
 * Esta clase se encarga de realizar llamadas HTTP al API de productos 
 * y gestionar las respuestas.
 */

@Slf4j
@Service
public class ProductoApiClientServiceImpl implements ProductoApiClientService {
  
  private final WebClientApi webClientApi;

  public ProductoApiClientServiceImpl(WebClientApi webClientApi) {
    this.webClientApi = webClientApi;
  }
  
  @Value("${app.apiSimpleId}")
  private String simpleID;

  @Value("${app.productosUrl}")
  private String productoUrl;
  
  @Value("${app.clientesUrl}")
  private String clientesUrl;
  
  @Value("${app.productoRolUrl}")
  private String productoRolUrl;
  
  @Override
  public Mono<ProductoRes> getProducto(String idProducto) {
    log.info(String.format("Consultando Api Producto : %s", idProducto));
    return webClientApi.getMono(productoUrl, String.format(this.simpleID, idProducto), 
      ProductoRes.class, String.format("Error al Buscar Producto: %s", idProducto));
  }

  @Override
  public Mono<ClienteRes> getCliente(String idCliente) {
    log.info(String.format("Consultando Api Cliente : %s", idCliente));
    return webClientApi.getMono(clientesUrl, String.format(this.simpleID, idCliente), 
      ClienteRes.class, String.format("Error al Buscar Cliente : %s", idCliente));
  }

  @Override
  public Mono<ProductoRolesRes> getProductoRoles(String idProducto) {
    log.info(String.format("Consultando Api Prodcuto - Roles : %s", idProducto));
    return webClientApi.getMono(productoUrl, String.format(this.productoRolUrl, idProducto), 
      ProductoRolesRes.class, String.format("Error al Buscar Producto Rol : %s", productoRolUrl));
  }
  
  

}
