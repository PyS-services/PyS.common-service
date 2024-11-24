package com.pys.common.java.service;

import com.pys.common.kotlin.exception.CotizacionException;
import com.pys.common.kotlin.model.Cotizacion;
import com.pys.common.kotlin.repository.CotizacionRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class CotizacionService {

    private final CotizacionRepository repository;

    public CotizacionService(CotizacionRepository repository) {
        this.repository = repository;
    }

    public Cotizacion findByFecha(OffsetDateTime fechaImportacion) {
        return repository.findByFecha(fechaImportacion).orElseThrow(() -> new CotizacionException(fechaImportacion));
    }

    public Cotizacion save(Cotizacion cotizacion) {
        return repository.save(cotizacion);
    }

}
