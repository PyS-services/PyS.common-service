package com.pys.common.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.pys.common.kotlin.model.audit.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
data class ArticuloImportado(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val articuloImportadoId: UUID? = null,

    val articuloId: UUID? = null,
    val fecha: OffsetDateTime? = null,
    val codigoArticulo: String? = null,
    val descripcion: String = "",
    val precioListaSinIva: BigDecimal = BigDecimal("0.00"),
    val origen: String = "",
    val descuento: String = "",

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val fechaActualizacion: OffsetDateTime,
    val cotizacionId: UUID? = null,
    val valorUsd: BigDecimal = BigDecimal("0.00"),
    val importacionId: UUID? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "artiuloId", insertable = false, updatable = false)
    var articulo: Articulo? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "cotizacionId", insertable = false, updatable = false)
    var cotizacion: Cotizacion? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "importacionId", insertable = false, updatable = false)
    var importacion: Importacion? = null,

) : Auditable() {

    class Builder {
        private var articuloImportadoId: UUID? = null
        private var articuloId: UUID? = null
        private var fecha: OffsetDateTime? = null
        private var codigoArticulo: String? = null
        private var descripcion: String = ""
        private var precioListaSinIva: BigDecimal = BigDecimal("0.00")
        private var origen: String = ""
        private var descuento: String = ""
        private var fechaActualizacion: OffsetDateTime = OffsetDateTime.now()
        private var cotizacionId: UUID? = null
        private var valorUsd: BigDecimal = BigDecimal("0.00")
        private var importacionId: UUID? = null
        private var articulo: Articulo? = null
        private var cotizacion: Cotizacion? = null
        private var importacion: Importacion? = null

        fun articuloImportadoId(articuloImportadoId: UUID?) = apply { this.articuloImportadoId = articuloImportadoId }
        fun articuloId(articuloId: UUID?) = apply { this.articuloId = articuloId }
        fun fecha(fecha: OffsetDateTime?) = apply { this.fecha = fecha }
        fun codigoArticulo(codigoArticulo: String?) = apply { this.codigoArticulo = codigoArticulo }
        fun descripcion(descripcion: String) = apply { this.descripcion = descripcion }
        fun precioListaSinIva(precioListaSinIva: BigDecimal) = apply { this.precioListaSinIva = precioListaSinIva }
        fun origen(origen: String) = apply { this.origen = origen }
        fun descuento(descuento: String) = apply { this.descuento = descuento }
        fun fechaActualizacion(fechaActualizacion: OffsetDateTime) = apply { this.fechaActualizacion = fechaActualizacion }
        fun cotizacionId(cotizacionId: UUID?) = apply { this.cotizacionId = cotizacionId }
        fun valorUsd(valorUsd: BigDecimal) = apply { this.valorUsd = valorUsd }
        fun importacionId(importacionId: UUID?) = apply { this.importacionId = importacionId }
        fun articulo(articulo: Articulo?) = apply { this.articulo = articulo }
        fun cotizacion(cotizacion: Cotizacion?) = apply { this.cotizacion = cotizacion }
        fun importacion(importacion: Importacion?) = apply { this.importacion = importacion }

        fun build() = ArticuloImportado(
            articuloImportadoId, articuloId, fecha, codigoArticulo, descripcion, precioListaSinIva, origen, descuento,
            fechaActualizacion, cotizacionId, valorUsd, importacionId, articulo, cotizacion, importacion
        )
    }
}
