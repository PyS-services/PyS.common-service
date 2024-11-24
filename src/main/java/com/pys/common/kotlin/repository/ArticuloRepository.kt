package com.pys.common.kotlin.repository

import com.pys.common.kotlin.model.Articulo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ArticuloRepository : JpaRepository<Articulo, Long> {

    fun findAllByCodigoArticuloIn(codigoArticulos: MutableList<String>): MutableList<Articulo?>?

    fun findByCodigoArticulo(codigoArticulo: String): Optional<Articulo?>?

}