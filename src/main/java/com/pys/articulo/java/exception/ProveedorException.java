package com.pys.articulo.java.exception;

public class ProveedorException extends RuntimeException {

    public ProveedorException(Long proveedorId) {
        super("Cannot found proveedor with id: " + proveedorId);
    }

}
