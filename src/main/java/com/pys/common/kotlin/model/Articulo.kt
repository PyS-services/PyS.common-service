package com.pys.common.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.pys.common.kotlin.model.audit.Auditable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["codigoArticulo"])])
data class Articulo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var articuloId: Long? = null,

    var codigoArticulo: String = "",
    var descripcion: String = "",
    var precioVentaConIva: BigDecimal = BigDecimal("0.00"),
    var precioVentaSinIva: BigDecimal = BigDecimal("0.00"),
    var precioListaConIva: BigDecimal = BigDecimal("0.00"),
    var precioListaSinIva: BigDecimal = BigDecimal("0.00"),
    var precioCompraSinIva: BigDecimal = BigDecimal("0.00"),
    var precioCompraSinIvaAnterior: BigDecimal = BigDecimal("0.00"),

    @Column(name = "flag_iva_105")
    var flagIva105: Byte = 0,
    var flagExento: Byte = 0,
    var modeloCamion: String = "",

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaActualizacion: OffsetDateTime? = null,
    var origen: String = "",
    var descuento: String = "",
    var proveedorId: Long? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var ultimaCompra: OffsetDateTime? = null,
    var marca: String = "",
    var catalogo: String = "",
    var precioListaSinIvaUsd: BigDecimal = BigDecimal("0.00"),
    var cotizacionId: Long? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "proveedorId", insertable = false, updatable = false)
    var proveedor: Proveedor? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "cotizacionId", insertable = false, updatable = false)
    var cotizacion: Cotizacion? = null,

) : Auditable() {

    class Builder {
        private var articuloId: Long? = null
        private var codigoArticulo: String = ""
        private var descripcion: String = ""
        private var precioVentaConIva: BigDecimal = BigDecimal("0.00")
        private var precioVentaSinIva: BigDecimal = BigDecimal("0.00")
        private var precioListaConIva: BigDecimal = BigDecimal("0.00")
        private var precioListaSinIva: BigDecimal = BigDecimal("0.00")
        private var precioCompraSinIva: BigDecimal = BigDecimal("0.00")
        private var precioCompraSinIvaAnterior: BigDecimal = BigDecimal("0.00")
        private var flagIva105: Byte = 0
        private var flagExento: Byte = 0
        private var modeloCamion: String = ""
        private var fechaActualizacion: OffsetDateTime? = null
        private var origen: String = ""
        private var descuento: String = ""
        private var proveedorId: Long? = null
        private var ultimaCompra: OffsetDateTime? = null
        private var marca: String = ""
        private var catalogo: String = ""
        private var precioListaSinIvaUsd: BigDecimal = BigDecimal("0.00")
        private var cotizacionId: Long? = null
        private var proveedor: Proveedor? = null

        fun articuloId(articuloId: Long?) = apply { this.articuloId = articuloId }
        fun codigoArticulo(codigoArticulo: String) = apply { this.codigoArticulo = codigoArticulo }
        fun descripcion(descripcion: String) = apply { this.descripcion = descripcion }
        fun precioVentaConIva(precioVentaConIva: BigDecimal) = apply { this.precioVentaConIva = precioVentaConIva }
        fun precioVentaSinIva(precioVentaSinIva: BigDecimal) = apply { this.precioVentaSinIva = precioVentaSinIva }
        fun precioListaConIva(precioListaConIva: BigDecimal) = apply { this.precioListaConIva = precioListaConIva }
        fun precioListaSinIva(precioListaSinIva: BigDecimal) = apply { this.precioListaSinIva = precioListaSinIva }
        fun precioCompraSinIva(precioCompraSinIva: BigDecimal) = apply { this.precioCompraSinIva = precioCompraSinIva }
        fun precioCompraSinIvaAnterior(precioCompraSinIvaAnterior: BigDecimal) = apply { this.precioCompraSinIvaAnterior = precioCompraSinIvaAnterior }
        fun flagIva105(flagIva105: Byte) = apply { this.flagIva105 = flagIva105 }
        fun flagExento(flagExento: Byte) = apply { this.flagExento = flagExento }
        fun modeloCamion(modeloCamion: String) = apply { this.modeloCamion = modeloCamion }
        fun fechaActualizacion(fechaActualizacion: OffsetDateTime?) = apply { this.fechaActualizacion = fechaActualizacion }
        fun origen(origen: String) = apply { this.origen = origen }
        fun descuento(descuento: String) = apply { this.descuento = descuento }
        fun proveedorId(proveedorId: Long?) = apply { this.proveedorId = proveedorId }
        fun ultimaCompra(ultimaCompra: OffsetDateTime?) = apply { this.ultimaCompra = ultimaCompra }
        fun marca(marca: String) = apply { this.marca = marca }
        fun catalogo(catalogo: String) = apply { this.catalogo = catalogo }
        fun precioListaSinIvaUsd(precioListaSinIvaUsd: BigDecimal) = apply { this.precioListaSinIvaUsd = precioListaSinIvaUsd }
        fun cotizacionId(cotizacionId: Long?) = apply { this.cotizacionId = cotizacionId }
        fun proveedor(proveedor: Proveedor?) = apply { this.proveedor = proveedor }

        fun build() = Articulo(
            articuloId, codigoArticulo, descripcion, precioVentaConIva, precioVentaSinIva, precioListaConIva,
            precioListaSinIva, precioCompraSinIva, precioCompraSinIvaAnterior, flagIva105, flagExento, modeloCamion,
            fechaActualizacion, origen, descuento, proveedorId, ultimaCompra, marca, catalogo,
            precioListaSinIvaUsd, cotizacionId, proveedor
        )
    }
}
