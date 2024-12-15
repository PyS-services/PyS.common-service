package com.pys.common.java.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pys.common.java.service.UsuarioNegocioService;
import com.pys.common.java.service.UsuarioService;
import com.pys.common.kotlin.exception.UsuarioException;
import com.pys.common.kotlin.model.Negocio;
import com.pys.common.kotlin.model.Usuario;
import com.pys.common.kotlin.model.UsuarioNegocio;
import com.pys.common.kotlin.model.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserService {

    private final UsuarioService usuarioService;
    private final UsuarioNegocioService usuarioNegocioService;

    public UserService(UsuarioService usuarioService,
                       UsuarioNegocioService usuarioNegocioService) {
        this.usuarioService = usuarioService;
        this.usuarioNegocioService = usuarioNegocioService;
    }

    public Boolean register(UserDto user) {
        var usuario = usuarioService.add(new Usuario.Builder()
                .login(user.getLogin())
                .password(DigestUtils.sha256Hex(user.getPassword()))
                .nombre(user.getNombre())
                .build());
        return usuario != null;
    }

    public List<Negocio> login(UserDto user) {
        log.debug("Processing login");
        try {
            Usuario usuario = usuarioService.findByLoginAndPassword(user.getLogin(), DigestUtils.sha256Hex(user.getPassword()));
            logUsuario(usuario);
            if (usuario == null) {
                return new ArrayList<>();
            }
            var negocios = usuarioNegocioService.findAllByUsuarioId(usuario.getUsuarioId()).stream()
                    .map(UsuarioNegocio::getNegocio).filter(Objects::nonNull)
                    .peek(negocio -> negocio.setRestricted(usuario.getRestricted() == 1))
                    .toList();
            logNegocios(negocios);
            return negocios;
        } catch (UsuarioException e) {
            return new ArrayList<>();
        }
    }

    public Boolean changePassword(UserDto user) {
        log.debug("Processing changePassword");
        try {
            var usuario = usuarioService.findByLoginAndPassword(user.getLogin(), DigestUtils.sha256Hex(user.getPassword()));
            logUsuario(usuario);
            if (usuario == null) {
                return false;
            }
            usuario = usuarioService.changePassword(usuario.getLogin(), DigestUtils.sha256Hex(user.getNewPassword()));
            logUsuario(usuario);
            return true;
        } catch (UsuarioException e) {
            return false;
        }
    }

    private void logNegocios(List<Negocio> negocios) {
        log.debug("Processing logNegocios");
        try {
            log.debug("negocios={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(negocios));
        } catch (JsonProcessingException e) {
            log.debug("negocios jsonify error -> {}", e.getMessage());
        }
    }

    private void logUsuario(Usuario usuario) {
        log.debug("Processing logUsuario");
        try {
            log.debug("usuario={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(usuario));
        } catch (JsonProcessingException e) {
            log.debug("usuario jsonify error -> {}", e.getMessage());
        }
    }

}
