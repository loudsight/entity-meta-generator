package com.loudsight.meta.serialization;

import com.loudsight.meta.serialization.transform.StringEntityTransform;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.agrona.DirectBuffer;

/**
 * Abstract base class for entity serialization and deserialization.
 * @param <T> the entity type
 */
public abstract class EntityTransform<T> {

    /**
     * The entity type associated with this transform.
     */
    protected final EntityType entityType;
    /**
     * The target classes this transform can handle.
     */
    private final List<Class<?>> targetClass;

    /**
     * Constructs an EntityTransform with the specified entity type and target classes.
     * @param entityType the entity type
     * @param targetClass the target classes
     */
    public EntityTransform(EntityType entityType, Class<?>... targetClass) {
        this.entityType = entityType;
        this.targetClass = Arrays.asList(targetClass);
    }

    /**
     * Gets the target classes.
     * @return list of target classes
     */
    public List<Class<?>> getTargetClass() {
        return targetClass;
    }

    /**
     * Checks if this transform can handle the given entity.
     * @param entity the entity to check
     * @return true if this transform can handle the entity
     */
    public boolean canTransform(Object entity) {
        return targetClass.stream().anyMatch(it -> it.isAssignableFrom(entity.getClass()));
    }

    /**
     * Serializes the entity to a list of bytes.
     * @param entity the entity to serialize
     * @param bytes the list to write bytes to
     */
    abstract public void serializeEntity(T entity, List<Byte> bytes);

    /**
     * Deserializes an entity from an iterator of bytes.
     * @param bytes the iterator of bytes
     * @return the deserialized entity
     */
    abstract public T deserializeEntity(Iterator<Byte> bytes);

    /**
     * Writes a string to the byte list.
     * @param str the string to write
     * @param bytes the byte list
     */
    protected void writeStr(String str, List<Byte> bytes) {
        var length = str.length();
        writeInt(length, bytes);
        Arrays.stream(str.split("")).forEach(e -> { bytes.add((byte)e.charAt(0)); });
    }

    /**
     * Writes a long to the byte list.
     * @param value the long value to write
     * @param bytes the byte list
     */
    protected void writeLong(long value, List<Byte> bytes) {
        var acc = value;
        for (int i = 0; i < 8; i++) {
            bytes.add(Long.valueOf(acc & 0xFF).byteValue());
            acc = acc >> 8;
        }
    }

    /**
     * Reads a long from the byte iterator.
     * @param bytes the byte iterator
     * @return the long value
     */
    protected long readLong(Iterator<Byte> bytes) {
        var value = 0L;
        for (int i = 0; i < 8; i++) {
            value += (Long.valueOf(bytes.next()) & 0xff) << i * 8;
        }

        return value;
    }

    /**
     * Writes an integer to the byte list.
     * @param length the integer value to write
     * @param bytes the byte list
     */
    protected void writeInt(int length, List<Byte> bytes) {
        for (int i = 0; i < 4; i++) {
            bytes.add(Integer.valueOf((length >> i * 8) & 0xFF).byteValue());
        }
    }

    /**
     * Reads an integer from the byte iterator.
     * @param bytes the byte iterator
     * @return the integer value
     */
    protected int readInt(Iterator<Byte> bytes) {
        var length = 0;
        for (int i  = 0; i  < 4; i ++) {
            length += ((Integer.valueOf(bytes.next()) & 0xff) << i * 8);
        }
        return length;
    }


//    companion object {
        /**
         * Reads a string from the byte iterator.
         * @param bytes the byte iterator
         * @return the deserialized string
         */
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

            return new String(primitiveBytes, Charset.defaultCharset());
        }
        /**
         * Serializes an entity to a byte array.
         * @param entity the entity to serialize
         * @return the byte array
         */
        public static <T> byte[] serialize(T entity) {
            var outBytes = new ArrayList<Byte>();
            serialize(entity, outBytes);

            var primitiveBytes = new byte[outBytes.size()];

            for (int i  = 0; i  < primitiveBytes.length; i ++) {
                primitiveBytes[i] = outBytes.get(i);
            }

            return primitiveBytes;
        }

        /**
         * Serializes an entity to a list of bytes.
         * @param entity the entity to serialize
         * @param bytes the byte list
         */
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

        /**
         * Deserializes an entity from a byte array.
         * @param bytes the byte array
         * @return the deserialized entity
         */
        public static <T> T deserialize(byte[] bytes) {
            return deserialize(bytes, 0, bytes.length);
        }

        /**
         * Deserializes an entity from a DirectBuffer.
         * @param buffer the DirectBuffer containing data
         * @param offset the offset in bytes
         * @param length the length in bytes
         * @return the deserialized entity
         */
        public static <T> T deserialize(DirectBuffer buffer, int offset, int length) {
            var byteIterator = new Iterator<Byte>() {
                int index = offset;
                final int endIndex = offset + length;

                @Override
                public boolean hasNext() {
                    return index < endIndex;
                }

                @Override
                public Byte next() {
                    if (index >= endIndex || index >= buffer.capacity()) {
                        System.err.println("DirectBuffer iterator error: index=" + index + ", endIndex=" + endIndex + ", capacity=" + buffer.capacity());
                        throw new NoSuchElementException();
                    }
                    var res = buffer.getByte(index);
                    index++;
                    return res;
                }
            };

            return deserialize(byteIterator);
        }

        /**
         * Deserializes an entity from a byte array with offset and length.
         * @param bytes the byte array
         * @param offset the offset
         * @param length the length
         * @return the deserialized entity
         */
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

    /**
     * Deserializes an entity from a byte iterator.
     * @param bytes the byte iterator
     * @return the deserialized entity
     */
    public static <T> T deserialize(Iterator<Byte> bytes) {
            var entityType = EntityType.getEntityType(bytes.next());
            var entityTransform = EntityTransforms.getInstance().getEntityTransform(entityType);

            return (T)entityTransform.deserializeEntity(bytes);
        }
    
    /**
     * Deserializes an entity from a byte iterator with fragment support.
     * This method can handle partial data and will return INCOMPLETE if more data is needed.
     * @param bytes the byte iterator
     * @return the fragment result
     */
    public static <T> FragmentResult<T> deserializeFragment(Iterator<Byte> bytes) {
        // Create a counting iterator to track bytes consumed
        var countingIterator = new CountingIterator(bytes);
        
        try {
            // Read entity type code (1 byte)
            if (!countingIterator.hasNext()) {
                return FragmentResult.incomplete(0);
            }
            byte entityTypeByte = countingIterator.next();
            
            var entityType = EntityType.getEntityType(entityTypeByte);
            var entityTransform = EntityTransforms.getInstance().getEntityTransform(entityType);
            
            // Try to deserialize the entity
            T entity = (T)entityTransform.deserializeEntity(countingIterator);
            
            return FragmentResult.success(entity, countingIterator.getCount());
            
        } catch (NoSuchElementException e) {
            // Ran out of data during deserialization
            return FragmentResult.incomplete(countingIterator.getCount());
        } catch (Exception e) {
            // Invalid data
            return FragmentResult.invalid(countingIterator.getCount());
        }
    }
    
    /**
     * Iterator wrapper that counts how many bytes have been consumed.
     */
    private static class CountingIterator implements Iterator<Byte> {
        private final Iterator<Byte> delegate;
        private int count = 0;
        
        public CountingIterator(Iterator<Byte> delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }
        
        @Override
        public Byte next() {
            if (!delegate.hasNext()) {
                throw new NoSuchElementException();
            }
            Byte b = delegate.next();
            count++;
            return b;
        }
        
        public int getCount() {
            return count;
        }
    }
//    }
}