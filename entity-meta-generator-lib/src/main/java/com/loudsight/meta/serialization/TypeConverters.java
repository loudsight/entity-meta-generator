package com.loudsight.meta.serialization;

import com.loudsight.useful.helper.ClassHelper;
import com.loudsight.useful.helper.JvmClassHelper;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public final class TypeConverters {
    private static final class TypeConvertersHolder {
        private static final TypeConverters INSTANCE = new TypeConverters();
    }
    // global access point
    public static TypeConverters getInstance() {
        return TypeConverters.TypeConvertersHolder.INSTANCE;
    }
    private static final Map<Class<?>, Map<Class<Object>, TypeConverter<?, ?>>> converters = new HashMap<>();

    private final Map<Class<Object>, TypeConverter<?, ?>> NO_CONVERTERS = new HashMap<>();

    private TypeConverters() {
        register(Integer.class, Long.class, from -> from != null ? from.intValue() : null);
        register(Class.class, String.class, JvmClassHelper::classForName);
        register(LocalDateTime.class, ZonedDateTime.class, LocalDateTime::from);
    }

    <T, F> void register(Class<T> to, Class<F> from, TypeConverter<T, F> converter) {
        var typeConverters = converters.compute(to, (k, v) -> new HashMap<>());
        typeConverters.put(ClassHelper.uncheckedCast(from), converter);
    }

    public  <T> T convert(Object from, Class<T> toType) {
        if (toType == from.getClass()) {
            return ClassHelper.uncheckedCast(from);
        }
        var fromConverter = converters.getOrDefault(toType, NO_CONVERTERS);
        var fromKClass = from.getClass();
        var toConverter = fromConverter.getOrDefault(fromKClass, new NoOpConverter());

        return ClassHelper.uncheckedCast(toConverter.convert(ClassHelper.uncheckedCast(from)));
    }

    static class NoOpConverter implements TypeConverter<Object, Object> {
        @Override
        public Object convert(Object from) {
            return from;
        }
    }
}
