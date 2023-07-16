package com.bestbank.movimientos.bussiness.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bestbank.movimientos.bussiness.client.WebClientApi;
import com.bestbank.movimientos.bussiness.dto.res.ClienteRes;
import com.bestbank.movimientos.bussiness.dto.res.ProductoRolesRes;
import com.bestbank.movimientos.bussiness.services.ProductoApiClientService;
import com.bestbank.movimientos.domain.model.Producto;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProductoApiClientServiceImpl implements ProductoApiClientService{

  @Value("${app.productosUrl}")
  private String productoUrl;
  
  @Value("${app.clientesUrl")
  private String clientesUrl;
  
  @Value("${app.productoRolUrl}")
  private String productoRolUrl;
  
  @Override
  public Mono<Producto> getProducto(String idProducto) {
    log.info(String.format("Consultando Api Producto : %s", idProducto));
    return WebClientApi.getMono(String.format(this.productoUrl, idProducto), 
        Producto.class, String.format("Error al Buscar Producto: %s", idProducto));
  }

  @Override
  public Mono<ClienteRes> getCliente(String idCliente) {
    log.info(String.format("Consultando Api Cliente : %s", idCliente));
    return WebClientApi.getMono(String.format(this.clientesUrl, idCliente), 
        ClienteRes.class, String.format("Error al Buscar Cliente : %s", idCliente));
  }

  @Override
  public Mono<ProductoRolesRes> getProductoRoles(String idProducto) {
    log.info(String.format("Consultando Api Prodcuto - Roles : %s", idProducto));
    return WebClientApi.getMono(String.format(this.productoRolUrl, idProducto), 
        ProductoRolesRes.class, String.format("Error al Buscar Producto Rol : %s", productoRolUrl));
  }
  
  

}
