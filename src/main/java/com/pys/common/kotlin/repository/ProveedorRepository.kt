package com.pys.common.kotlin.repository

import com.pys.common.kotlin.model.Proveedor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface ProveedorRepository : JpaRepository<Proveedor, UUID>, JpaSpecificationExecutor<Proveedor> {

    fun findAllByOrderByRazonSocial(): List<Proveedor>

    fun findAllByOrderByRazonSocial(pageable: Pageable): Page<Proveedor>

    fun findAllByRazonSocialContainsIgnoreCaseOrderByRazonSocial(razonSocial: String, pageable: Pageable): Page<Proveedor>

    fun findByProveedorId(proveedorId: UUID): Optional<Proveedor?>?

    fun findByProveedorIdNegocio(proveedorIdNegocio: Long): Optional<Proveedor?>?

    fun findTop1ByOrderByProveedorIdNegocioDesc(): Optional<Proveedor?>?

    fun findByCuit(cuit: String): Optional<Proveedor?>?

    fun deleteByProveedorId(proveedorId: UUID)

}