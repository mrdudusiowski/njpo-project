package com.cpma.app.model.response

data class JWTResponse(var accessToken: String, var tokenType: String, var id: Long, var name: String, var surname: String, var username: String, var email: String, var phone: String, var roles: List<String>)