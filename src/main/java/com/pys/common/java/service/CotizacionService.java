package com.pys.common.java.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pys.common.kotlin.exception.CotizacionException;
import com.pys.common.kotlin.model.Cotizacion;
import com.pys.common.kotlin.repository.CotizacionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CotizacionService {

    private final CotizacionRepository repository;

    public CotizacionService(CotizacionRepository repository) {
        this.repository = repository;
    }

    public List<Cotizacion> findAll() {
        return repository.findAll();
    }

    public Cotizacion findByFecha(OffsetDateTime fecha) {
        log.debug("Processing cotizacion for findByFecha");
        return Objects.requireNonNull(repository.findByFecha(fecha)).orElseThrow(() -> new CotizacionException(fecha));
    }

    public Cotizacion add(Cotizacion cotizacion) {
        log.debug("Processing cotizacion for add");
        logCotizacion(cotizacion);
        cotizacion = repository.save(cotizacion);
        logCotizacion(cotizacion);
        return cotizacion;
    }

    public Cotizacion update(Cotizacion cotizacion, OffsetDateTime fecha) {
        log.debug("Processing cotizacion for update");
        return repository.findByFecha(fecha).map(existingCotizacion -> {
            existingCotizacion = new Cotizacion.Builder()
                    .cotizacionId(existingCotizacion.getCotizacionId())
                    .fecha(cotizacion.getFecha())
                    .usdCompra(cotizacion.getUsdCompra())
                    .usdVenta(cotizacion.getUsdVenta())
                    .build();
            existingCotizacion = repository.save(existingCotizacion);
            logCotizacion(existingCotizacion);
            return existingCotizacion;
        }).orElseThrow(() -> new CotizacionException(fecha));
    }

    public Cotizacion addOrUpdate(Cotizacion cotizacion) {
        log.debug("Processing cotizacion for addOrUpdate");
        return repository.findByFecha(Objects.requireNonNull(cotizacion.getFecha())).map(existingCotizacion -> update(cotizacion, cotizacion.getFecha()))
                .orElseGet(() -> add(cotizacion));
    }

    private void logCotizacion(Cotizacion cotizacion) {
        try {
            log.debug("Cotizacion -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cotizacion));
        } catch (JsonProcessingException e) {
            log.error("Cotizacion jsonify error -> {}", e.getMessage());
        }
    }

}
