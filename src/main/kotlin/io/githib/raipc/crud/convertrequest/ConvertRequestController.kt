package io.githib.raipc.crud.convertrequest;

import io.githib.raipc.crud.idempotency.IdempotencyService
import io.githib.raipc.crud.util.CustomHeaders
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.Instant

@RestController
@RequestMapping("/converter/requests")
class ConvertRequestController(
    val convertRequestService: ConvertRequestService,
    val idempotencyService: IdempotencyService<ConvertRequestModel, ConvertRequestDto>
) {
    @PostMapping("/new")
    fun putNewRequest(
        @RequestBody request: ConvertRequestModel,
        @RequestHeader(CustomHeaders.IDEMPOTENCY_KEY) key: String?
    ): ConvertRequestDto = if (key == null) {
        convertRequestService.insert(request.toDto())
    } else {
        idempotencyService.process(key, request) {
            convertRequestService.insert(it.toDto())
        }
    }

    @GetMapping("", produces = ["text/csv"])
    fun listAll() = convertRequestService.listAll()

    @GetMapping("/{id}")
    fun findRequestById(@PathVariable id: Long) = convertRequestService.findById(id)

    @PutMapping("/{id}")
    fun updateRequestById(@PathVariable id: Long, @RequestBody request: ConvertRequestModel) =
        convertRequestService.update(id, request.toDto())

    @DeleteMapping("/{id}")
    fun deleteRequestById(@PathVariable id: Long) =
        convertRequestService.deleteById(id)
}

data class ConvertRequestModel(var from: String, var to: String, var quantity: BigDecimal, var rate: BigDecimal) {
    fun toDto() = ConvertRequestDto(0, from, to, quantity, rate, Instant.now())
}
