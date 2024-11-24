package com.pys.common.kotlin.exception

import java.time.OffsetDateTime

class CotizacionException(fecha: OffsetDateTime) : RuntimeException("Cotizacion no encontrada para fecha $fecha")