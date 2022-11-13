package io.githib.raipc.crud.exchange

interface CurrencyExchangeService {
    fun getExchange(from: String, to: String): CurrencyExchange
}
