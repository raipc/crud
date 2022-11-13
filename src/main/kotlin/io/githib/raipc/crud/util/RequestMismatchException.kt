package io.githib.raipc.crud.util

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
class RequestMismatchException(key: String) : RuntimeException("Reused Idempotency-Key $key with different payload")
