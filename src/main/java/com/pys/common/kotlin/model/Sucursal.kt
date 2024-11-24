package com.pys.common.kotlin.model

import com.pys.common.kotlin.model.audit.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Sucursal(

    @Id
    var sucursalId: Int? = null,
    var nombre: String? = null

) : Auditable()
