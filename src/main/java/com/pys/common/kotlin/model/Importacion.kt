package com.pys.common.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.pys.common.kotlin.model.audit.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.OffsetDateTime
import java.util.UUID

@Entity
data class Importacion(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val importacionId: UUID? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val fechaImportacion: OffsetDateTime? = null

) : Auditable() {

    class Builder {
        private var importacionId: UUID? = null
        private var fechaImportacion: OffsetDateTime? = null

        fun importacionId(importacionId: UUID?) = apply { this.importacionId = importacionId }
        fun fechaImportacion(fechaImportacion: OffsetDateTime?) = apply { this.fechaImportacion = fechaImportacion }

        fun build() = Importacion(
            importacionId, fechaImportacion
        )
    }
}
