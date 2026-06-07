package com.loudsight.meta.entity;

import com.loudsight.meta.annotation.Introspect;

@Introspect(clazz = SimpleParentClass.class)
public class SimpleParentClass extends SimpleClass {
    private int y;


    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
