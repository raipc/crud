package io.githib.raipc.crud.util

interface ModelConverter<Entity, DTO> {
    fun toEntity(dto: DTO) : Entity
    fun toDTO(entity: Entity) : DTO
}