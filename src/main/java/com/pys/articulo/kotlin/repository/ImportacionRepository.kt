package com.pys.articulo.kotlin.repository

import com.pys.articulo.kotlin.model.Importacion
import org.springframework.data.jpa.repository.JpaRepository

interface ImportacionRepository : JpaRepository<Importacion, Long>