package com.loudsight.meta.serialization;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing different entity types for serialization.
 */
public enum EntityType {
    /**
     * Null type.
     */
    NULL('a'),
    /**
     * Byte type.
     */
    BYTE('b'),
    /**
     * Bytes type.
     */
    BYTES('c'),
    /**
     * Integer type.
     */
    INTEGER('d'),
    /**
     * Long type.
     */
    LONG('e'),
    /**
     * String type.
     */
    STRING('f'),
    /**
     * Double type.
     */
    DOUBLE('g'),
    /**
     * Float type.
     */
    FLOAT('h'),
    /**
     * List type.
     */
    LIST('i'),
    /**
     * Set type.
     */
    SET('j'),
    /**
     * Array type.
     */
    ARRAY('k'),
    /**
     * Map type.
     */
    MAP('l'),
    /**
     * Boolean type.
     */
    BOOLEAN('m'),
    /**
     * Enum type.
     */
    ENUM('n'),
    /**
     * Custom type.
     */
    CUSTOM('o'),
    /**
     * Class type.
     */
    CLASS('p'),
    /**
     * DateTime type.
     */
    DATETIME('q');
    /**
     * The byte code for this entity type.
     */
    private final byte code;

    /**
     * Constructs an EntityType with the given character code.
     * @param code the character code
     */
    EntityType(char code) {
        this.code = (byte)code;
    }

    private static final Map<Byte, EntityType> codeToEntityType = new HashMap<>();

    static {

        for (var entityType : values()) {
            codeToEntityType.put(entityType.code, entityType);
        }
    }

    /**
     * Gets the byte code for this entity type.
     * @return the byte code
     */
    public byte getCode() {
        return code;
    }

    /**
     * Gets the EntityType for the given byte code.
     * @param code the byte code
     * @return the EntityType
     */
    public static EntityType getEntityType(byte code) {
        var res = codeToEntityType.get(code);

        if (res == null) {
            throw new IllegalArgumentException("Unknown entity type code: " + code);
        }

        return res;
    }
}
