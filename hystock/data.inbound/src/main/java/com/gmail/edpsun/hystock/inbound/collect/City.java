package com.gmail.edpsun.hystock.inbound.collect;

import java.io.Serializable;

public class City implements Serializable {
    public static final long serialVersionUID = 6392376146163510146L;
    public String name;

    public City(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s is a city in the country %s with a population of %s", name, name, name);
    }
}
