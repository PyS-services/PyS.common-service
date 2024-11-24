package com.pys.common.kotlin.exception

class ProveedorException(proveedorId: Long) : RuntimeException("Proveedor no encontrado con id $proveedorId")