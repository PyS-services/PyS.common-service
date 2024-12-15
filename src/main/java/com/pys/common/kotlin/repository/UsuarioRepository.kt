package com.pys.common.kotlin.repository

import com.pys.common.kotlin.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UsuarioRepository : JpaRepository<Usuario, Long> {

    fun findByLogin(login: String): Optional<Usuario?>?

    fun findByLoginAndPassword(login: String, password: String): Optional<Usuario?>?

}