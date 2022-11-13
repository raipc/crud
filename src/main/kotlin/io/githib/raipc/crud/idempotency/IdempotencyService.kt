package io.githib.raipc.crud.idempotency

interface IdempotencyService<Req, Resp> {
    fun process(key: String, request: Req, action: (Req) -> Resp): Resp
}
