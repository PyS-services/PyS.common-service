package com.pys.articulo.java.exception;

import java.time.OffsetDateTime;

public class CotizacionException extends RuntimeException{

    public CotizacionException(OffsetDateTime fechaImportacion) {
        super("Cannot find Cotizacion for " + fechaImportacion);
    }

}
