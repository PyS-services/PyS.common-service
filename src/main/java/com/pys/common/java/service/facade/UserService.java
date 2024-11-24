package com.pys.common.java.service.facade;

import com.pys.common.java.service.UsuarioNegocioService;
import com.pys.common.java.service.UsuarioService;
import com.pys.common.kotlin.exception.UsuarioException;
import com.pys.common.kotlin.model.Negocio;
import com.pys.common.kotlin.model.Usuario;
import com.pys.common.kotlin.model.UsuarioNegocio;
import com.pys.common.kotlin.model.dto.UserDto;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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
        try {
            Usuario usuario = usuarioService.findByLoginAndPassword(user.getLogin(), DigestUtils.sha256Hex(user.getPassword()));
            if (usuario == null) {
                return new ArrayList<>();
            }
            return usuarioNegocioService.findAllByUsuarioId(usuario.getUsuarioId()).stream()
                    .map(UsuarioNegocio::getNegocio)
                    .toList();
        } catch (UsuarioException e) {
            return new ArrayList<>();
        }
    }

}
