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

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(columnNames = ["usuarioId", "negocioId"])]
)
data class UsuarioNegocio(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val usuarioNegocioId: Int? = null,
    val usuarioId: Int? = null,
    val negocioId: Short? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "negocioId", insertable = false, updatable = false)
    val negocio: Negocio? = null

) : Auditable()