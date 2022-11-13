package io.githib.raipc.crud.convertrequest

import io.githib.raipc.crud.exchange.CurrencyExchange
import java.math.BigDecimal
import java.time.Instant
import javax.persistence.*

@Entity
data class ConvertRequest(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
                          @ManyToOne var exchange: CurrencyExchange,
                          var quantity: BigDecimal,
                          var rate: BigDecimal,
                          var userId: Long,
                          var createdAt: Instant?
) {
    constructor() : this(null, CurrencyExchange.EMPTY,  BigDecimal.ZERO, BigDecimal.ZERO, 0L, null)
}

