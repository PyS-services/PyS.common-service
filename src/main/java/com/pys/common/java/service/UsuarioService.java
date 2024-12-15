package com.pys.common.java.service;

import com.pys.common.kotlin.exception.UsuarioException;
import com.pys.common.kotlin.model.Usuario;
import com.pys.common.kotlin.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario add(Usuario usuario) {
        return repository.save(usuario);
    }

    public Usuario findByLoginAndPassword(String login, String password) {
        log.debug("Processing findByLoginAndPassword");
        return Objects.requireNonNull(repository.findByLoginAndPassword(login, password)).orElseThrow(() -> new UsuarioException(login));
    }

    public Usuario changePassword(String login, String newPassword) {
        log.debug("Processing changePassword");
        var usuario = Objects.requireNonNull(repository.findByLogin(login)).orElseThrow(() -> new UsuarioException(login));
        assert usuario != null;
        usuario.setPassword(newPassword);
        return repository.save(usuario);
    }
}
