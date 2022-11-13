package io.githib.raipc.crud.util

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ResourceNotFoundException(resourceType: String, resourceName: String) :
    RuntimeException("$resourceType '${resourceName}' not found")