package io.githib.raipc.crud.currency

interface CurrencyService {
    fun getByCode(code: String): Currency
}
