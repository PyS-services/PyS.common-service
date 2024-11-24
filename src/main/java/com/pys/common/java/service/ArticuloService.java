package com.pys.common.java.service;

import com.pys.common.kotlin.exception.ArticuloException;
import com.pys.common.kotlin.model.Articulo;
import com.pys.common.kotlin.repository.ArticuloRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
        return repository.findByCodigoArticulo(codigoArticulo).orElseThrow(() -> new ArticuloException(codigoArticulo));
    }

    public List<Articulo> saveAll(List<Articulo> articulos) {
        return repository.saveAll(articulos);
    }

}
