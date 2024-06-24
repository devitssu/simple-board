package com.teamsparta.simpleboard.api.exception

class ModelNotFoundException(model: String, id: Long) : RuntimeException(
    message = "$model does not exist with id: $id",
)