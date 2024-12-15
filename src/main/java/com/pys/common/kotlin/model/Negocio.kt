package com.pys.common.kotlin.model

import com.pys.common.kotlin.model.audit.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Transient

@Entity
data class Negocio(

    @Id
    val negocioId: Short? = null,
    val nombre: String? = null,
    var sucursalId: Int? = null,
    var databaseName: String? = null,
    var databaseIp: String? = null,
    var databaseDsn: String? = null,
    var databaseUid: String? = null,
    var databasePwd: String? = null,
    var backendIp: String? = null,
    var backendPort: Int = 0,
    var puntoVenta: Int? = null,
    var prefixAudit: String? = null,
    
    @Transient
    var restricted: Boolean = false

) : Auditable()