package com.pys.common.kotlin.repository

import com.pys.common.kotlin.model.Cotizacion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.Optional

@Repository
interface CotizacionRepository : JpaRepository<Cotizacion, Long> {

    fun findByFecha(fechaImportacion: OffsetDateTime): Optional<Cotizacion?>?

}