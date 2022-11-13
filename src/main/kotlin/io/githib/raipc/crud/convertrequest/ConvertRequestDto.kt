package io.githib.raipc.crud.convertrequest

import java.math.BigDecimal
import java.time.Instant

data class ConvertRequestDto(
    var id: Long?,
    var from: String,
    var to: String,
    var quantity: BigDecimal,
    var rate: BigDecimal,
    var createdAt: Instant?
)
