package com.pys.common.kotlin.repository

import com.pys.common.kotlin.model.Cotizacion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID

@Repository
interface CotizacionRepository : JpaRepository<Cotizacion, UUID> {

    fun findByFecha(fechaImportacion: OffsetDateTime): Optional<Cotizacion?>?

}