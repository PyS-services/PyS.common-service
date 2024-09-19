package com.pys.articulo.kotlin.repository

import com.pys.articulo.kotlin.model.ArticuloImportado
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticuloImportadoRepository : JpaRepository<ArticuloImportado, Long> {
}