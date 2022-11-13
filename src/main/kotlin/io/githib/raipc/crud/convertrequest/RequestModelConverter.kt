package io.githib.raipc.crud.convertrequest

import io.githib.raipc.crud.exchange.CurrencyExchangeService
import io.githib.raipc.crud.util.ModelConverter
import org.springframework.stereotype.Service

@Service
class RequestModelConverter(private val exchangeService: CurrencyExchangeService) :
    ModelConverter<ConvertRequest, ConvertRequestDto> {
    private val testUserId = 42L

    override fun toEntity(dto: ConvertRequestDto): ConvertRequest {
        return ConvertRequest(
            id = null,
            exchange = exchangeService.getExchange(dto.from, dto.to),
            quantity = dto.quantity,
            rate = dto.rate,
            userId = testUserId,
            createdAt = dto.createdAt
        )
    }

    override fun toDTO(entity: ConvertRequest): ConvertRequestDto {
        val exchange = entity.exchange
        return ConvertRequestDto(
            id = entity.id,
            from = exchange.from.code,
            to = exchange.to.code,
            rate = entity.rate,
            quantity = entity.quantity,
            createdAt = entity.createdAt
        )
    }
}