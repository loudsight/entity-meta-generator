package com.loudsight.meta.entity

import com.loudsight.meta.annotation.Id

class EntityAnnotation(@field:Id val name: String, vararg annotationValues: AnnotationValue) {
    class AnnotationValue(@field:Id internal val name: String, val value: Any)

    val properties: Map<String, AnnotationValue?>

    init {
        properties = annotationValues.associateBy({ it.name  }, {  it })
    }
}