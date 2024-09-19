package com.pys.articulo.java.service;

import com.pys.articulo.java.exception.ProveedorException;
import com.pys.articulo.kotlin.model.Proveedor;
import com.pys.articulo.kotlin.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

@Service
public class ProveedorService {

    private final ProveedorRepository repository;

    public ProveedorService(ProveedorRepository repository) {
        this.repository = repository;
    }

    public Proveedor findByProveedorId(Long proveedorId) {
        return repository.findByProveedorId(proveedorId).orElseThrow(() -> new ProveedorException(proveedorId));
    }
}
