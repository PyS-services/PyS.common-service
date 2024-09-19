package com.pys.articulo.kotlin.repository

import com.pys.articulo.kotlin.model.Articulo
import com.pys.articulo.kotlin.model.Proveedor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ArticuloRepository : JpaRepository<Articulo, Long> {

    fun findAllByCodigoArticuloIn(codigoArticulos: MutableList<String>): MutableList<Articulo?>?

    fun findByCodigoArticulo(codigoArticulo: String): Optional<Articulo?>?

}