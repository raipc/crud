package io.githib.raipc.crud.convertrequest

import org.springframework.data.repository.Repository

interface ConvertRequestRepository : Repository<ConvertRequest, Long> {
    fun findById(id: Long): ConvertRequest?
    fun existsById(id: Long): Boolean
    fun findAll(): List<ConvertRequest>
    fun deleteById(id: Long)
    fun save(request: ConvertRequest): ConvertRequest
}
