package io.githib.raipc.crud.currency

import org.springframework.data.repository.Repository

interface CurrencyRepository: Repository<Currency, Int> {
    fun findByCode(code: String): Currency?
}
