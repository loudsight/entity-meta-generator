package com.loudsight.meta.serialization;

/**
 * Result of fragment-based deserialization.
 * @param <T> the type of the deserialized entity
 */
public class FragmentResult<T> {
    
    /**
     * Status of the fragment deserialization.
     */
    public enum Status {
        /** Complete entity successfully deserialized */
        SUCCESS,
        /** Incomplete - need more data to complete deserialization */
        INCOMPLETE,
        /** Invalid data - cannot deserialize */
        INVALID
    }
    
    private final Status status;
    private final T entity;
    private final int bytesConsumed;
    
    private FragmentResult(Status status, T entity, int bytesConsumed) {
        this.status = status;
        this.entity = entity;
        this.bytesConsumed = bytesConsumed;
    }
    
    /**
     * Creates a successful result with a complete entity.
     * @param entity the deserialized entity
     * @param bytesConsumed number of bytes consumed
     * @param <T> entity type
     * @return successful fragment result
     */
    public static <T> FragmentResult<T> success(T entity, int bytesConsumed) {
        return new FragmentResult<>(Status.SUCCESS, entity, bytesConsumed);
    }
    
    /**
     * Creates an incomplete result indicating more data is needed.
     * @param bytesConsumed number of bytes consumed so far
     * @param <T> entity type
     * @return incomplete fragment result
     */
    public static <T> FragmentResult<T> incomplete(int bytesConsumed) {
        return new FragmentResult<>(Status.INCOMPLETE, null, bytesConsumed);
    }
    
    /**
     * Creates an invalid result for malformed data.
     * @param bytesConsumed number of bytes consumed before failure
     * @param <T> entity type
     * @return invalid fragment result
     */
    public static <T> FragmentResult<T> invalid(int bytesConsumed) {
        return new FragmentResult<>(Status.INVALID, null, bytesConsumed);
    }
    
    /**
     * @return the status of the deserialization
     */
    public Status getStatus() {
        return status;
    }
    
    /**
     * @return the deserialized entity (null if not successful)
     */
    public T getEntity() {
        return entity;
    }
    
    /**
     * @return number of bytes consumed from the iterator
     */
    public int getBytesConsumed() {
        return bytesConsumed;
    }
    
    /**
     * @return true if deserialization was successful
     */
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
    
    /**
     * @return true if more data is needed
     */
    public boolean isIncomplete() {
        return status == Status.INCOMPLETE;
    }
    
    /**
     * @return true if data is invalid
     */
    public boolean isInvalid() {
        return status == Status.INVALID;
    }
}
