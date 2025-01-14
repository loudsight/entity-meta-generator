package com.loudsight.meta.entity;

import com.loudsight.meta.annotation.Introspect;
import java.util.List;
import java.util.Objects;

@Introspect(clazz = SimpleClass.class)
public class SimpleClass {
    private int i;
    private double d;
    private String s;
    private List<String> strings;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleClass that = (SimpleClass) o;
        return i == that.i && Double.compare(that.d, d) == 0 && Objects.equals(s, that.s) && Objects.equals(strings, that.strings);
    }
}
