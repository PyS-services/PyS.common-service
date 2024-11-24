package com.pys.common.kotlin.exception

class UsuarioException(login: String) : RuntimeException("Usuario no encontrado con login $login")