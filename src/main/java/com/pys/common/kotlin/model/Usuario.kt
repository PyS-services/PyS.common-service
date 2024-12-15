package com.pys.common.kotlin.model

import com.pys.common.kotlin.model.audit.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Usuario(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val usuarioId: Int? = null,
    val login: String = "",
    var password: String = "",
    val nombre: String = "",
    val restricted: Byte = 0,

) : Auditable() {
    class Builder {
        private var usuarioId: Int? = null
        private var login: String = ""
        private var password: String = ""
        private var nombre: String = ""
        private var restricted: Byte = 0

        fun usuarioId(usuarioId: Int?) = apply { this.usuarioId = usuarioId }
        fun login(login: String) = apply { this.login = login }
        fun password(password: String) = apply { this.password = password }
        fun nombre(nombre: String) = apply { this.nombre = nombre }
        fun restricted(restricted: Byte) = apply { this.restricted = restricted }

        fun build() = Usuario(
            usuarioId, login, password, nombre, restricted
        )
    }

}