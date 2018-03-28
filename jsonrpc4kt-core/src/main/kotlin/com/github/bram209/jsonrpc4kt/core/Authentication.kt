package com.github.bram209.jsonrpc4kt.core

abstract class AuthMode

object NoAuth : AuthMode()

data class BasicAuth(val username: String, val password: String) : AuthMode()

data class Authentication(internal var authMode: AuthMode = NoAuth)
