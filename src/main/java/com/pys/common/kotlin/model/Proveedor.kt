package com.pys.common.kotlin.model

import com.pys.common.kotlin.model.audit.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["cuit"])])
data class Proveedor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val proveedorId: Long? = null,
    val razonSocial: String = "",
    val nombreFantasia: String = "",
    val cuit: String = "",
    val domicilio: String = "",
    val localidad: String = "",
    val provincia: String = "",
    val telefono: String = "",
    val fax: String = "",
    val email: String = "",
    val posicionIva: Short = 0,
    val celular: String = "",
    val ingresosBrutos: String = "",
    val contacto: String = "",
    val observaciones: String = "",

) : Auditable() {

    class Builder {
        private var proveedorId: Long? = null
        private var razonSocial: String = ""
        private var nombreFantasia: String = ""
        private var cuit: String = ""
        private var domicilio: String = ""
        private var localidad: String = ""
        private var provincia: String = ""
        private var telefono: String = ""
        private var fax: String = ""
        private var email: String = ""
        private var posicionIva: Short = 0
        private var celular: String = ""
        private var ingresosBrutos: String = ""
        private var contacto: String = ""
        private var observaciones: String = ""

        fun proveedorId(proveedorId: Long?) = apply { this.proveedorId = proveedorId }
        fun razonSocial(razonSocial: String) = apply { this.razonSocial = razonSocial }
        fun nombreFantasia(nombreFantasia: String) = apply { this.nombreFantasia = nombreFantasia }
        fun cuit(cuit: String) = apply { this.cuit = cuit }
        fun domicilio(domicilio: String) = apply { this.domicilio = domicilio }
        fun localidad(localidad: String) = apply { this.localidad = localidad }
        fun provincia(provincia: String) = apply { this.provincia = provincia }
        fun telefono(telefono: String) = apply { this.telefono = telefono }
        fun fax(fax: String) = apply { this.fax = fax }
        fun email(email: String) = apply { this.email = email }
        fun posicionIva(posicionIva: Short) = apply { this.posicionIva = posicionIva }
        fun celular(celular: String) = apply { this.celular = celular }
        fun ingresosBrutos(ingresosBrutos: String) = apply { this.ingresosBrutos = ingresosBrutos }
        fun contacto(contacto: String) = apply { this.contacto = contacto }
        fun observaciones(observaciones: String) = apply { this.observaciones = observaciones }

        fun build() = Proveedor(
            proveedorId, razonSocial, nombreFantasia, cuit, domicilio, localidad, provincia, telefono, fax, email,
            posicionIva, celular, ingresosBrutos, contacto, observaciones
        )
    }
}
