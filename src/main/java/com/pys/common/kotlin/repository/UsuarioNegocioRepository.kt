package com.pys.common.kotlin.repository

import com.pys.common.kotlin.model.UsuarioNegocio
import org.springframework.data.jpa.repository.JpaRepository

interface UsuarioNegocioRepository : JpaRepository<UsuarioNegocio, Long> {

    fun findAllByUsuarioId(usuarioId: Int): List<UsuarioNegocio>

}