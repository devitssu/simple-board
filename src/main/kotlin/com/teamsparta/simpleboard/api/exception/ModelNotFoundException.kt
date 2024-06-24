package com.teamsparta.simpleboard.api.exception

class ModelNotFoundException(model: String, id: Long) : RuntimeException(
    "$model does not exist with id: $id"
)