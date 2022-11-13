package io.githib.raipc.crud.exchange

import io.githib.raipc.crud.currency.Currency
import jakarta.persistence.*

@Entity
data class CurrencyExchange(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int,
    @ManyToOne var from: Currency,
    @ManyToOne var to: Currency
) {
    constructor() : this(0, Currency.EMPTY, Currency.EMPTY)

    companion object {
        val EMPTY = CurrencyExchange()
    }
}