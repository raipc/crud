package io.githib.raipc.crud.idempotency

import io.githib.raipc.crud.util.ConflictingRequestException
import io.githib.raipc.crud.util.RequestMismatchException
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

@Service
class IdempotencyServiceImpl<Req, Resp> : IdempotencyService<Req, Resp> {
    private val map = ConcurrentHashMap<String, IdempotenceRequestAndResponse>()

    override fun process(key: String, request: Req, action: (Req) -> Resp): Resp {
        val container = IdempotenceRequestAndResponse(request as Any)
        val prev = map.putIfAbsent(key, container)
        if (prev != null) {
            if (prev.request != request) {
                throw RequestMismatchException(key)
            }
            val result = prev.result.get() ?: throw ConflictingRequestException(key)
            if (result is Exception) throw result
            @Suppress("UNCHECKED_CAST")
            return result as Resp
        } else {
            try {
                return action(request).also { container.result.set(it) }
            } catch (e: Exception) {
                container.result.set(e)
                throw e
            }
        }
    }

    data class IdempotenceRequestAndResponse(val request: Any, var result: AtomicReference<Any?> = AtomicReference())
}