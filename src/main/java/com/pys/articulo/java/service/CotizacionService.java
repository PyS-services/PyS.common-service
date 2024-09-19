package com.pys.articulo.java.service;

import com.pys.articulo.java.exception.CotizacionException;
import com.pys.articulo.kotlin.model.Cotizacion;
import com.pys.articulo.kotlin.repository.CotizacionRepository;
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
