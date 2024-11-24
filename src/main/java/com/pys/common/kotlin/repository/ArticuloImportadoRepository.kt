package com.pys.common.kotlin.repository

import com.pys.common.kotlin.model.ArticuloImportado
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticuloImportadoRepository : JpaRepository<ArticuloImportado, Long> {
}