package com.pys.common.java.service;

import com.pys.common.kotlin.model.UsuarioNegocio;
import com.pys.common.kotlin.repository.UsuarioNegocioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioNegocioService {

    private final UsuarioNegocioRepository repository;

    public UsuarioNegocioService(UsuarioNegocioRepository repository) {
        this.repository = repository;
    }

    public List<UsuarioNegocio> findAllByUsuarioId(Integer usuarioId) {
        return repository.findAllByUsuarioId(usuarioId);
    }

}
