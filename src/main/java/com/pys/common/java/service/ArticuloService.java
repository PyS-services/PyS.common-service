package com.pys.common.java.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pys.common.kotlin.exception.ArticuloException;
import com.pys.common.kotlin.model.Articulo;
import com.pys.common.kotlin.repository.ArticuloRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ArticuloService {

    private final ArticuloRepository repository;

    public ArticuloService(ArticuloRepository repository) {
        this.repository = repository;
    }

    public List<Articulo> findAll() {
        return repository.findAll();
    }

    public List<Articulo> findAllByCodigos(List<String> codigoArticulos) {
        return repository.findAllByCodigoArticuloIn(codigoArticulos);
    }

    public Articulo findByCodigoArticulo(String codigoArticulo) {
        log.debug("Processing request for findByCodigoArticulo");
        Articulo articulo = repository.findByCodigoArticulo(codigoArticulo)
                .orElseThrow(() -> new ArticuloException(codigoArticulo));
        logArticulo(articulo);
        return articulo;
    }

    public List<Articulo> saveAll(List<Articulo> articulos) {
        return repository.saveAll(articulos);
    }

    public Articulo add(Articulo articulo) {
        log.debug("Processing request for add");
        articulo = repository.save(articulo);
        logArticulo(articulo);
        return articulo;
    }

    public Articulo update(Articulo newArticulo, String codigoArticulo) {
        log.debug("Processing request for update");
        return repository.findByCodigoArticulo(codigoArticulo).map(articulo -> {
            articulo = new Articulo.Builder()
                    .articuloId(articulo.getArticuloId())
                    .codigoArticulo(newArticulo.getCodigoArticulo())
                    .descripcion(newArticulo.getDescripcion())
                    .precioVentaConIva(newArticulo.getPrecioVentaConIva())
                    .precioVentaSinIva(newArticulo.getPrecioVentaSinIva())
                    .precioListaConIva(newArticulo.getPrecioListaConIva())
                    .precioListaSinIva(newArticulo.getPrecioListaSinIva())
                    .precioCompraSinIva(newArticulo.getPrecioCompraSinIva())
                    .precioCompraSinIvaAnterior(newArticulo.getPrecioCompraSinIvaAnterior())
                    .flagIva105(newArticulo.getFlagIva105())
                    .flagExento(newArticulo.getFlagExento())
                    .modeloCamion(newArticulo.getModeloCamion())
                    .fechaActualizacion(newArticulo.getFechaActualizacion())
                    .origen(newArticulo.getOrigen())
                    .descuento(newArticulo.getDescuento())
                    .proveedorId(newArticulo.getProveedorId())
                    .ultimaCompra(newArticulo.getUltimaCompra())
                    .marca(newArticulo.getMarca())
                    .precioListaSinIvaUsd(newArticulo.getPrecioListaSinIvaUsd())
                    .cotizacionId(newArticulo.getCotizacionId())
                    .build();
            articulo = repository.save(articulo);
            logArticulo(articulo);
            return articulo;
        }).orElseThrow(() -> new ArticuloException(codigoArticulo));
    }

    public Articulo addOrUpdate(Articulo articulo) {
        log.debug("Processing request for addOrUpdate");
        return repository.findByCodigoArticulo(articulo.getCodigoArticulo())
                .map(existingArticulo -> update(articulo, articulo.getCodigoArticulo()))
                .orElseGet(() -> add(articulo));
    }

    private void logArticulo(Articulo articulo) {
        try {
            log.debug("Articulo -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(articulo));
        } catch (JsonProcessingException e) {
            log.error("Articulo jsonify error -> {}", e.getMessage());
        }
    }

}
