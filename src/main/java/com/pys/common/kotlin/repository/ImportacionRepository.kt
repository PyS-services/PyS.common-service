package com.pys.common.kotlin.repository

import com.pys.common.kotlin.model.Importacion
import org.springframework.data.jpa.repository.JpaRepository

interface ImportacionRepository : JpaRepository<Importacion, Long>