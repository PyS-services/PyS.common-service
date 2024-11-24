package com.pys.common.java.service;

import com.pys.common.kotlin.model.ArticuloImportado;
import com.pys.common.kotlin.repository.ArticuloImportadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticuloImportadoService {

    private final ArticuloImportadoRepository repository;

    public ArticuloImportadoService(ArticuloImportadoRepository repository) {
        this.repository = repository;
    }

    public List<ArticuloImportado> saveAll(List<ArticuloImportado> articulosImported) {
        return repository.saveAll(articulosImported);
    }

}
