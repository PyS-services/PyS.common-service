package com.pys.common.java.service;

import com.pys.common.kotlin.model.Importacion;
import com.pys.common.kotlin.repository.ImportacionRepository;
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
