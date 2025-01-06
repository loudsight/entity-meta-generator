package com.loudsight.meta.serialization;

import java.util.HashMap;
import java.util.Map;

public enum EntityType {
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
    DATETIME('q');
    private final byte code;

    EntityType(char code) {
        this.code = (byte)code;
    }

    private static final Map<Byte, EntityType> codeToEntityType = new HashMap<>();

    static {

        for (var entityType : values()) {
            codeToEntityType.put(entityType.code, entityType);
        }
    }

    public byte getCode() {
        return code;
    }

    public static EntityType getEntityType(byte code) {
        var res = codeToEntityType.get(code);

        if (res == null) {
            throw new IllegalArgumentException("Unknown entity type code: " + code);
        }

        return res;
    }
}
