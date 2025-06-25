package com.pys.common.java.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pys.common.kotlin.exception.ArticuloAliasException;
import com.pys.common.kotlin.model.ArticuloAlias;
import com.pys.common.kotlin.repository.ArticuloAliasRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArticuloAliasService {

    private final ArticuloAliasRepository repository;

    public ArticuloAliasService(ArticuloAliasRepository repository) {
        this.repository = repository;
    }

    public ArticuloAlias add(ArticuloAlias articuloAlias) {
        log.debug("Processing request for add");
        articuloAlias = repository.save(articuloAlias);
        logArticuloAlias(articuloAlias);
        return articuloAlias;
    }

    public ArticuloAlias update(ArticuloAlias newArticuloAlias, String alias) {
        log.debug("Processing request for update");
        return repository.findByAlias(alias).map(articuloAlias -> {
            articuloAlias = new ArticuloAlias.Builder()
                    .articuloAliasId(articuloAlias.getArticuloAliasId())
                    .articuloId(newArticuloAlias.getArticuloId())
                    .alias(newArticuloAlias.getAlias())
                    .proveedorId(newArticuloAlias.getProveedorId())
                    .precioCompra(newArticuloAlias.getPrecioCompra())
                    .build();
            articuloAlias = repository.save(articuloAlias);
            logArticuloAlias(articuloAlias);
            return articuloAlias;
        }).orElseThrow(() -> new ArticuloAliasException(alias));
    }

    public ArticuloAlias addOrUpdate(ArticuloAlias articuloAlias) {
        log.debug("Processing request for addOrUpdate");
        return repository.findByAlias(articuloAlias.getAlias())
                .map(existingArticuloAlias -> update(articuloAlias, articuloAlias.getAlias()))
                .orElseGet(() -> add(articuloAlias));
    }

    private void logArticuloAlias(ArticuloAlias articuloAlias) {
        try {
            log.debug("ArticuloAlias -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(articuloAlias));
        } catch (JsonProcessingException e) {
            log.error("ArticuloAlias jsonify error -> {}", e.getMessage());
        }
    }

}
