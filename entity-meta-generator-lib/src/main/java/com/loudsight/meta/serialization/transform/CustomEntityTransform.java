package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.Meta;
import com.loudsight.meta.MetaRepository;
import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CustomEntityTransform extends EntityTransform<Object> { 

    private static class CustomEntityTransformHolder {
        private static final CustomEntityTransform INSTANCE = new CustomEntityTransform();
    }
    // global access point
    public static CustomEntityTransform getInstance() {
        return CustomEntityTransform.CustomEntityTransformHolder.INSTANCE;
    }
    private CustomEntityTransform() {
        super(EntityType.CUSTOM, Object.class);
    }


    @Override public void serializeEntity( Object entity, List<Byte> bytes) {
        serializeEntityX(entity, bytes);
    }

    private <T> void serializeEntityX(T entity, List<Byte> bytes) {

        var meta = MetaRepository.getInstance().getMeta((Class<T>)entity.getClass());
        if (meta == null) {
            throw new IllegalArgumentException("Unknown entity class: " + entity.getClass().getSimpleName());
        }

        bytes.add(EntityType.CUSTOM.getCode());
        writeStr(meta.getTypeName(), bytes);

        var fieldBytes = new ArrayList<Byte>();

//        var subtypeFields = meta.typeHierarchy
//            .map { MetaRepository.getMeta(it) as Meta<T> }
//            .flatMap { it.fields }
//
//        var fields = Stream.concat(subtypeFields.stream(), meta.fields.stream())
//            .collect(Collectors.toList())


        var fieldCount = meta.getFields()
                .stream()
            .filter( it -> it.get(entity) != null)
            .map(it -> {
                writeStr(it.getName(), fieldBytes);
                var fieldValue = it.get(entity);
                serialize(fieldValue, fieldBytes);
                return 0;
            }).count();
        
        writeInt(Long.valueOf(fieldCount).intValue(), bytes);
        bytes.addAll(fieldBytes);
    }

    @Override public Object  deserializeEntity(Iterator<Byte> bytes) {
        var typeName = readStr(bytes);
        Meta<Object> meta = MetaRepository.getInstance().getMeta(typeName);
        if (meta == null) {
            throw new IllegalArgumentException("Unknown entity type-name: " + typeName);
        }
        var fieldCount = readInt(bytes);
        var fieldMap = new HashMap<String, Object>();
//        fieldMap["__className__"] = typeName

        for (int i  = 0; i  < fieldCount; i ++) {
            var fieldName = readStr(bytes);
            var fieldValue = deserialize(bytes);
            fieldMap.put(fieldName, fieldValue);
        }

        return meta.newInstance(fieldMap);
    }


    @Override public  boolean canTransform(Object entity) {
        throw new IllegalStateException("Unexpected call to CustomEntityTransform.canTransform");
    }

}
