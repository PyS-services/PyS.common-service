package com.pys.articulo.java.exception;

public class ArticuloException extends RuntimeException {

    public ArticuloException(String codigoArticulo) {
        super("Cannot find Articulo with code: " + codigoArticulo);
    }

}
