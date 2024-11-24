package com.pys.common.kotlin.repository

import com.pys.common.kotlin.model.Proveedor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProveedorRepository : JpaRepository<Proveedor, Long> {

    fun findByProveedorId(proveedorId: Long): Optional<Proveedor?>?

}