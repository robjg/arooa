package org.oddjob.arooa.design.layout;

import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingWithEveryLayout {

    private String name;

    private String a;

    private String b;

    private String c;

    private String d;

    private String e;

    private Object single;

    private Map<String, Object> mapped = new HashMap<>();

    private List<Object> indexed = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public Object getSingle() {
        return single;
    }

    public void setSingle(Object single) {
        this.single = single;
    }

    public void setIndexed(int index, Object object) {
        new ListSetterHelper(indexed).set(index, object);
    }

    public List<Object> getIndexedList() {
        return indexed;
    }

    public void setMapped(String key, Object object) {
        if (object == null) {
            mapped.remove(key);
        }
        else {
            mapped.put(key, object);
        }
    }

    public Map<String, Object> getMappeMap() {
        return mapped;
    }
}
