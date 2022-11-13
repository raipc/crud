package io.githib.raipc.crud.currency

import io.githib.raipc.crud.util.ResourceNotFoundException
import org.springframework.stereotype.Service

@Service
class CurrencyServiceImpl(private var currencyRepository: CurrencyRepository): CurrencyService {
    override fun getByCode(code: String): Currency {
        return currencyRepository.findByCode(code) ?: throw ResourceNotFoundException("Currency", code)
    }
}