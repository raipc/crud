package io.githib.raipc.crud.util

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT)
class ConflictingRequestException(key: String) :
    RuntimeException("Request with Idempotency-Key $key is still processing")