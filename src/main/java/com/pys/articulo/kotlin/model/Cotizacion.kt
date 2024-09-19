package com.pys.articulo.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.pys.articulo.kotlin.model.audit.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(uniqueConstraints = [
    UniqueConstraint(columnNames = ["fecha"])
])
data class Cotizacion(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cotizacionId: Long? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,
    var usdCompra: BigDecimal = BigDecimal("0.00"),
    var usdVenta: BigDecimal = BigDecimal("0.00"),

) : Auditable() {

    class Builder {
        private var cotizacionId: Long? = null
        private var fecha: OffsetDateTime? = null
        private var usdCompra: BigDecimal = BigDecimal("0.00")
        private var usdVenta: BigDecimal = BigDecimal("0.00")

        fun cotizacionId(cotizacionId: Long?) = apply { this.cotizacionId = cotizacionId }
        fun fecha(fecha: OffsetDateTime?) = apply { this.fecha = fecha }
        fun usdCompra(usdCompra: BigDecimal) = apply { this.usdCompra = usdCompra }
        fun usdVenta(usdVenta: BigDecimal) = apply { this.usdVenta = usdVenta }

        fun build() = Cotizacion(
            cotizacionId, fecha, usdCompra, usdVenta
        )
    }
}
