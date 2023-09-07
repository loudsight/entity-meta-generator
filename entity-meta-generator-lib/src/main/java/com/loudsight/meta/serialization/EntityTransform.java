package com.loudsight.meta.serialization;

import com.loudsight.meta.serialization.transform.StringEntityTransform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class EntityTransform<T> {

    protected final EntityType entityType;
    private final Class<T> targetClass;

    public EntityTransform(EntityType entityType, Class<T> targetClass) {
        this.entityType = entityType;
        this.targetClass = targetClass;
    }

    public Class<T> getTargetClass() {
        return targetClass;
    }

    public boolean canTransform(Object entity) {
        return targetClass.isAssignableFrom(entity.getClass());
    }

    abstract public void serializeEntity(T entity, List<Byte> bytes);

    abstract public T deserializeEntity(Iterator<Byte> bytes);

    protected void writeStr(String str, List<Byte> bytes) {
        var length = str.length();
        writeInt(length, bytes);
        Arrays.stream(str.split("")).forEach(e -> { bytes.add((byte)e.charAt(0)); });
    }

    protected void writeLong(long value, List<Byte> bytes) {
        var acc = value;
        for (int i = 0; i < 8; i++) {
            bytes.add(Long.valueOf(acc & 0xFF).byteValue());
            acc = acc >> 8;
        }
    }

    protected long readLong(Iterator<Byte> bytes) {
        var value = 0L;
        for (int i = 0; i < 8; i++) {
            value += (Long.valueOf(bytes.next()) & 0xff) << i * 8;
        }

        return value;
    }

    protected void writeInt(int length, List<Byte> bytes) {
        for (int i = 0; i < 4; i++) {
            bytes.add(Integer.valueOf((length >> i * 8) & 0xFF).byteValue());
        }
    }

    protected int readInt(Iterator<Byte> bytes) {
        var length = 0;
        for (int i  = 0; i  < 4; i ++) {
            length += Integer.valueOf(bytes.next()) & 0xff >> i * 8;
        }
        return length;
    }


//    companion object {
        public static String  readStr(Iterator<Byte> bytes) {
            var length = StringEntityTransform.getInstance().readInt(bytes);
            var outBytes = new ArrayList<Byte>();
            for (int i  = 0; i  < length; i ++) {
                outBytes.add(bytes.next());
            }
            // todo fixme
            var primitiveBytes = new byte[outBytes.size()];

            for (int i  = 0; i  < length; i ++) {
                primitiveBytes[i] = outBytes.get(i);
            }

            return new String(primitiveBytes);
        }
        public static <T> byte[] serialize(T entity) {
            var outBytes = new ArrayList<Byte>();
            serialize(entity, outBytes);

            var primitiveBytes = new byte[outBytes.size()];

            for (int i  = 0; i  < primitiveBytes.length; i ++) {
                primitiveBytes[i] = outBytes.get(i);
            }

            return primitiveBytes;
        }

        public static <T> void serialize(T entity, List<Byte> bytes) {
            if (entity == null) {
                bytes.add(EntityType.NULL.getCode());
                return;
            }
            EntityTransform<T> entityTransform =  EntityTransforms.getInstance().getEntityTransform(entity);

            if (entityTransform != null) {
                entityTransform.serializeEntity(entity, bytes);
            } else {
                throw new IllegalStateException("No transformer found for " + entity.getClass());
            }
        }

        public static <T> T deserialize(byte[] bytes) {
            return deserialize(bytes, 0, bytes.length);
        }

        public static <T> T deserialize(byte[] bytes, int offset, int length) {
            var byteIterator = new Iterator<Byte>() {
                int index = offset;

                @Override
                public boolean hasNext() {
                    return index < length;
                }

                @Override
                public Byte next() {
                    var res = bytes[index];
                    index++;
                    return res;
                }
            };

            return deserialize(byteIterator);
        }

    public static <T> T deserialize(Iterator<Byte> bytes) {
            var entityType = EntityType.getEntityType(bytes.next());
            var entityTransform = EntityTransforms.getInstance().getEntityTransform(entityType);

            return (T)entityTransform.deserializeEntity(bytes);
        }
//    }
}