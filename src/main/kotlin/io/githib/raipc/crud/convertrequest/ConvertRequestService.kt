package io.githib.raipc.crud.convertrequest

interface ConvertRequestService {
    fun listAll() : List<ConvertRequestDto>
    fun findById(id: Long) : ConvertRequestDto
    fun insert(request: ConvertRequestDto): ConvertRequestDto
    fun update(id: Long, request: ConvertRequestDto): ConvertRequestDto
    fun deleteById(id: Long)
}
