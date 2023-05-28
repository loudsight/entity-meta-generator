package com.loudsight.meta;

import com.loudsight.collection.MultiKeyMap;
import com.loudsight.useful.helper.ClassHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public class EntityHelper {
    //    companion object {
    private static final Logger logger = LoggerFactory.getLogger(EntityHelper.class);

    private static final List<Class<?>> primitives = List.of(
            boolean.class,
            Boolean.class,
            int.class,
            Integer.class,
            double.class,
            Double.class,
            long.class,
            Long.class,
            String.class,
            LocalDateTime.class
    );

    public interface Converter<F, T> extends Function<F, T> {

    }

    private static final MultiKeyMap<Class<?>, Class<?>, Converter<Object, ?>> converters = new MultiKeyMap<>();

    static {
        converters.put(Integer.class, Long.class, it -> Long.valueOf((Integer)it));
    }

    public static boolean isPrimitive(Class<?> entity) {
        return primitives.contains(entity);
    }

    public static MultiKeyMap<Class<?>, Class<?>, Converter<Object, ?>> converters() {
//            return java.util.Map.of<Pair<Class<?>, Class<?>?>, Function<?, ?>>(
//                Pair<Class<?>, Class<?>?>(Long.class.java, Int.class.javaPrimitiveType),
//                Function<Long, Any> { obj: Long -> obj.toInt() } as Function<Long, ?>
//            )
        return converters;
    }

    public static <T> T convert(Object value, Class<T> targetClass) {

        var result = value;

        try {
            Class<?> sourceClass = value.getClass();

            if (targetClass.isAssignableFrom(sourceClass)) {
                return ClassHelper.Companion.uncheckedCast(result);
            }

            if (Enum.class.isAssignableFrom(targetClass)) {
                    result = targetClass.getMethod("valueOf", String.class).invoke(null, value.toString());
                    return ClassHelper.Companion.uncheckedCast(result);
            }

            Converter<Object, T> converter = (Converter<Object, T>) converters.get(sourceClass, targetClass);

            if (converter != null) {
                return converter.apply(value);
            }
//                if (Enum.class.java.isAssignableFrom(targetClass)) {
//                    result = targetClass.getMethod("valueOf", String.class.java).invoke(null, value.toString())
//                    return uncheckedCast(result)
//                }
//                if (Int.class.javaPrimitiveType!!.isAssignableFrom(targetClass)) {
//                    result = Integer.valueOf(value.toString())
//                    return uncheckedCast(result)
//                }
//                if (Long.class.javaPrimitiveType!!.isAssignableFrom(targetClass)) {
//                    result = java.lang.Long.valueOf(value.toString())
//                    return uncheckedCast(result);
//                }
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        }
        return ClassHelper.Companion.uncheckedCast(result);
    }
}