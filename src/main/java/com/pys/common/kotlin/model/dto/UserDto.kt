package com.pys.common.kotlin.model.dto

data class UserDto (

    val login: String = "",
    val password: String = "",
    val nombre: String = "",
    var newPassword: String = ""

)