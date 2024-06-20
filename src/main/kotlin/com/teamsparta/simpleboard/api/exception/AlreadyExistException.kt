package com.teamsparta.simpleboard.api.exception

class AlreadyExistException(private val name: String) : RuntimeException("중복된 ${name}입니다.")
