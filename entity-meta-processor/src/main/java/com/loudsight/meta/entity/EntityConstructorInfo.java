package com.loudsight.meta.entity;

import java.util.List;

public record EntityConstructorInfo(
        List<EntityVariableInfo> entityParameterInfos
) {

}