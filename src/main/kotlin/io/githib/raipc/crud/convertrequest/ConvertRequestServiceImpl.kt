package io.githib.raipc.crud.convertrequest

import io.githib.raipc.crud.util.ModelConverter
import io.githib.raipc.crud.util.ResourceNotFoundException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConvertRequestServiceImpl(
    private val convertRequestRepository: ConvertRequestRepository,
    private val modelConverter: ModelConverter<ConvertRequest, ConvertRequestDto>
) : ConvertRequestService {
    override fun listAll(): List<ConvertRequestDto> = convertRequestRepository.findAll()
        .map { modelConverter.toDTO(it) }

    override fun findById(id: Long): ConvertRequestDto =
        convertRequestRepository.findById(id)?.let { modelConverter.toDTO(it) }
            ?: throw ResourceNotFoundException("Request", "id=$id")

    override fun deleteById(id: Long) {
        try {
            convertRequestRepository.deleteById(id)
        } catch (e: EmptyResultDataAccessException) {
            throw ResourceNotFoundException("Request", "id=$id")
        }
    }

    @Transactional
    override fun insert(request: ConvertRequestDto): ConvertRequestDto {
        val entity = convertRequestRepository.save(modelConverter.toEntity(request))
        return modelConverter.toDTO(entity)
    }

    @Transactional
    override fun update(id: Long, request: ConvertRequestDto): ConvertRequestDto {
        if (!convertRequestRepository.existsById(id)) {
            throw ResourceNotFoundException("Request", "id=$id")
        }
        val entity = modelConverter.toEntity(request).apply { this.id = id }
        return modelConverter.toDTO(convertRequestRepository.save(entity))
    }
}