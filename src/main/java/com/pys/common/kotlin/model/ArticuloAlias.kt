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

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["alias"])])
data class ArticuloAlias(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val articuloAliasId: Long? = null,
    val articuloId: Long? = null,
    val alias: String = "",
    val proveedorId: Long? = null,
    val precioCompra: BigDecimal = BigDecimal("0.00"),

    @OneToOne(optional = true)
    @JoinColumn(name = "articuloId", insertable = false, updatable = false)
    val articulo: Articulo? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "proveedorId", insertable = false, updatable = false)
    val proveedor: Proveedor? = null,

) : Auditable() {

    class Builder {
        private var articuloAliasId: Long? = null
        private var articuloId: Long? = null
        private var alias: String = ""
        private var proveedorId: Long? = null
        private var precioCompra: BigDecimal = BigDecimal("0.00")
        private var articulo: Articulo? = null
        private var proveedor: Proveedor? = null

        fun articuloAliasId(articuloAliasId: Long?) = apply { this.articuloAliasId = articuloAliasId }
        fun articuloId(articuloId: Long?) = apply { this.articuloId = articuloId }
        fun alias(alias: String) = apply { this.alias = alias }
        fun proveedorId(proveedorId: Long?) = apply { this.proveedorId = proveedorId }
        fun precioCompra(precioCompra: BigDecimal) = apply { this.precioCompra = precioCompra }
        fun articulo(articulo: Articulo?) = apply { this.articulo = articulo }
        fun proveedor(proveedor: Proveedor?) = apply { this.proveedor = proveedor }

        fun build() = ArticuloAlias(
            articuloAliasId, articuloId, alias, proveedorId, precioCompra, articulo, proveedor
        )
    }
}
