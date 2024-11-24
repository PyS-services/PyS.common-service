package com.pys.common.kotlin.repository

import com.pys.common.kotlin.model.ArticuloAlias
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticuloAliasRepository : JpaRepository<ArticuloAlias, Long> {
}