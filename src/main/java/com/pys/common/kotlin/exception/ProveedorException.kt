package com.pys.common.kotlin.exception

import java.util.UUID

class ProveedorException : RuntimeException {

    constructor() : super("Ningún proveedor encontrado")
    constructor(proveedorId: UUID) : super("Proveedor no encontrado con id $proveedorId")
    constructor(proveedorIdNegocio: Long) : super("Proveedor no encontrado con idNegocio $proveedorIdNegocio")
    constructor(cuit: String) : super("Proveedor no encontrado con cuit $cuit")

}