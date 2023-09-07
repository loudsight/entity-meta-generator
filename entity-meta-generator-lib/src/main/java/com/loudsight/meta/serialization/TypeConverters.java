package com.loudsight.meta.serialization;

import com.loudsight.useful.helper.ClassHelper;

import java.util.HashMap;
import java.util.Map;

public class TypeConverters {
    private static class TypeConvertersHolder {
        private static final TypeConverters INSTANCE = new TypeConverters();
    }
    // global access point
    public static TypeConverters getInstance() {
        return TypeConverters.TypeConvertersHolder.INSTANCE;
    }
    private TypeConverters() {}

    private static final Map<Class<?>, Map<Class<Object>, TypeConverter<?, ?>>> converters = new HashMap<>();

    private final Map<Class<Object>, TypeConverter<?, ?>> NO_CONVERTERS = new HashMap<>();

    {
        register(Integer.class, Long.class, from -> from != null ? from.intValue() : null);
    }

    <T, F> void register(Class<T> to, Class<F> from, TypeConverter<T, F> converter) {
        var typeConverters = converters.compute(to, (k, v) -> new HashMap<>());
        typeConverters.put(ClassHelper.uncheckedCast(from), converter);
    }

    public  <T> T convert(Object from, Class<T> toType) {
        if (toType == from.getClass()) {
            return (T)from;
        }
        var fromConverter = converters.getOrDefault(toType, NO_CONVERTERS);
        var fromKClass = from.getClass();
        var toConverter = fromConverter.getOrDefault(fromKClass, new NoOpConverter());

        return (T)toConverter.convert(ClassHelper.uncheckedCast(from));
    }

    class NoOpConverter implements TypeConverter<Object, Object> {
        @Override
        public Object convert(Object from) {
            return from;
        }
    }
}
