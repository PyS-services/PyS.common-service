package com.pys.common.kotlin.exception

class ArticuloAliasException : RuntimeException {
    constructor(alias: String) : super("ArticuloAlias no encontrado con alias $alias")
}