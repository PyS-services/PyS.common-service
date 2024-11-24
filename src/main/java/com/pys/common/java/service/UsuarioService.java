package com.pys.common.java.service;

import com.pys.common.kotlin.exception.UsuarioException;
import com.pys.common.kotlin.model.Usuario;
import com.pys.common.kotlin.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario add(Usuario usuario) {
        return repository.save(usuario);
    }

    public Usuario findByLoginAndPassword(String login, String password) {
        return Objects.requireNonNull(repository.findByLoginAndPassword(login, password)).orElseThrow(() -> new UsuarioException(login));
    }

}
