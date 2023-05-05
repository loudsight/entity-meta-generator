package com.loudsight.meta.serialization

enum class EntityType(val code: Byte) {
    NULL('a'),
    BYTE('b'),
    BYTES('c'),
    INTEGER('d'),
    LONG('e'),
    STRING('f'),
    DOUBLE('g'),
    FLOAT('h'),
    LIST('i'),
    SET('j'),
    ARRAY('k'),
    MAP('l'),
    BOOLEAN('m'),
    ENUM('n'),
    CUSTOM('o'),
    CLASS('p'),
    DATETIME('q')
    ;

    constructor(code: Char) : this(code.code.toByte())

    companion object {
        val codeToEntityType = HashMap<Char, EntityType>()

        init {
            for (entityType in values()) {
                codeToEntityType[entityType.code.toChar()] = entityType
            }
        }
        fun getEntityType(code: Char): EntityType {
            return codeToEntityType[code] ?: throw IllegalArgumentException("Unknown entity type code: $code")
        }
    }
}