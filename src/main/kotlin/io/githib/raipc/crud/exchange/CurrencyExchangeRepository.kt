package io.githib.raipc.crud.exchange

import io.githib.raipc.crud.currency.Currency
import org.springframework.data.repository.Repository

interface CurrencyExchangeRepository: Repository<CurrencyExchange, Int> {
    fun findByFromAndTo(from: Currency, to: Currency): CurrencyExchange?
}
