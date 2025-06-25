package com.pys.common.kotlin.exception

import java.time.OffsetDateTime

class CotizacionException : RuntimeException {
    constructor(fecha: OffsetDateTime) : super("Cotizacion no encontrada para fecha $fecha")
}