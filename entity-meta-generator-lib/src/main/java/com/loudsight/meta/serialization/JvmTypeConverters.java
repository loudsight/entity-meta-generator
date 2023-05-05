package com.loudsight.meta.serialization;

import com.loudsight.useful.helper.JvmClassHelper;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class JvmTypeConverters {
    public static void register() {
        TypeConverters.INSTANCE.register(
                JvmClassHelper.toKClass(Class.class),
                JvmClassHelper.toKClass(String.class),
                JvmClassHelper::classForName);
        TypeConverters.INSTANCE.register(
                JvmClassHelper.toKClass(LocalDateTime.class),
                JvmClassHelper.toKClass(ZonedDateTime.class),
                LocalDateTime::from);
    }
}
