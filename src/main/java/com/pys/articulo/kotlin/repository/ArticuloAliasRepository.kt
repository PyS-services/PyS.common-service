package com.pys.articulo.kotlin.repository

import com.pys.articulo.kotlin.model.ArticuloAlias
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticuloAliasRepository : JpaRepository<ArticuloAlias, Long> {
}