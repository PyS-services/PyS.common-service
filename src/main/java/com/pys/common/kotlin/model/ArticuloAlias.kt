package com.pys.common.kotlin.model

import com.pys.common.kotlin.model.audit.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["alias"])])
data class ArticuloAlias(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val articuloAliasId: UUID? = null,
    val articuloId: UUID? = null,
    val alias: String = "",
    val proveedorId: UUID? = null,
    val precioCompra: BigDecimal = BigDecimal("0.00"),

    @OneToOne(optional = true)
    @JoinColumn(name = "articuloId", insertable = false, updatable = false)
    val articulo: Articulo? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "proveedorId", insertable = false, updatable = false)
    val proveedor: Proveedor? = null,

) : Auditable() {

    class Builder {
        private var articuloAliasId: UUID? = null
        private var articuloId: UUID? = null
        private var alias: String = ""
        private var proveedorId: UUID? = null
        private var precioCompra: BigDecimal = BigDecimal("0.00")
        private var articulo: Articulo? = null
        private var proveedor: Proveedor? = null

        fun articuloAliasId(articuloAliasId: UUID?) = apply { this.articuloAliasId = articuloAliasId }
        fun articuloId(articuloId: UUID?) = apply { this.articuloId = articuloId }
        fun alias(alias: String) = apply { this.alias = alias }
        fun proveedorId(proveedorId: UUID?) = apply { this.proveedorId = proveedorId }
        fun precioCompra(precioCompra: BigDecimal) = apply { this.precioCompra = precioCompra }
        fun articulo(articulo: Articulo?) = apply { this.articulo = articulo }
        fun proveedor(proveedor: Proveedor?) = apply { this.proveedor = proveedor }

        fun build() = ArticuloAlias(
            articuloAliasId, articuloId, alias, proveedorId, precioCompra, articulo, proveedor
        )
    }
}
