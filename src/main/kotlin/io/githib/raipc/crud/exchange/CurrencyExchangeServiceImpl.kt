package io.githib.raipc.crud.exchange

import io.githib.raipc.crud.currency.CurrencyService
import io.githib.raipc.crud.util.ResourceNotFoundException
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class CurrencyExchangeServiceImpl(
    private val currencyExchangeRepository: CurrencyExchangeRepository,
    private val currencyService: CurrencyService
): CurrencyExchangeService {
    @Cacheable("currencyExchange")
    override fun getExchange(from: String, to: String): CurrencyExchange {
        val fromCurrency = currencyService.getByCode(from)
        val toCurrency = currencyService.getByCode(to)
        return currencyExchangeRepository.findByFromAndTo(fromCurrency, toCurrency)
            ?: throw ResourceNotFoundException("CurrencyExchange", "$from/$to")
    }
}