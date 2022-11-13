package io.githib.raipc.crud.currency

import jakarta.persistence.*

@Entity
data class Currency(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int,
    var code: String,
    var name: String,
    @Enumerated(EnumType.STRING) var type: Type
) {
    constructor() : this(0, "", "", Type.FIAT)

    enum class Type {
        FIAT, CRYPTO
    }

    companion object {
        val EMPTY = Currency()
    }
}