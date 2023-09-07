package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransforms;

public class JvmTransforms {
    public static void init() {
        EntityTransforms.getInstance().register(TemporalEntityTransform.getInstance());
    }
}
