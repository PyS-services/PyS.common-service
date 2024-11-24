package com.pys.common.kotlin.exception

class ArticuloException(codigoArticulo: String) : RuntimeException("Articulo no encontrado con codigo $codigoArticulo")
