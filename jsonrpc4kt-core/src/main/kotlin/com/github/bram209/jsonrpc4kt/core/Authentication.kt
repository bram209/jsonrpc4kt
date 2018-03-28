package com.github.bram209.jsonrpc4kt.core

abstract class AuthMode
object NoAuth : AuthMode()

data class Authentication(internal var authMode: AuthMode = NoAuth)

data class BasicAuth(val username: String, val password: String) : AuthMode()

fun Authentication.basicAuth(username: String, password: String) {
    authMode = BasicAuth(username, password)
}
