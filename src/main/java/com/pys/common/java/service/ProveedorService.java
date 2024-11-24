package com.pys.common.java.service;

import com.pys.common.kotlin.exception.ProveedorException;
import com.pys.common.kotlin.model.Proveedor;
import com.pys.common.kotlin.repository.ProveedorRepository;
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
