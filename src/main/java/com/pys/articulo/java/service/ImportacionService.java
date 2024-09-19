package com.pys.articulo.java.service;

import com.pys.articulo.kotlin.model.Importacion;
import com.pys.articulo.kotlin.repository.ImportacionRepository;
import org.springframework.stereotype.Service;

@Service
public class ImportacionService {

    private final ImportacionRepository repository;

    public ImportacionService(ImportacionRepository repository) {
        this.repository = repository;
    }

    public Importacion add(Importacion importacion) {
        return repository.save(importacion);
    }

}
