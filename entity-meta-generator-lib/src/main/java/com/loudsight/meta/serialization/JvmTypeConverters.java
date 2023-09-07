package com.loudsight.meta.serialization;


import com.loudsight.useful.helper.JvmClassHelper;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class JvmTypeConverters {
    public static void register() {
        TypeConverters.getInstance().register(
                Class.class,
                String.class,
                JvmClassHelper::classForName);
        TypeConverters.getInstance().register(
                LocalDateTime.class,
                ZonedDateTime.class,
                LocalDateTime::from);
    }
}
