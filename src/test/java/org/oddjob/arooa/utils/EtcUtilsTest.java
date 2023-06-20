package org.oddjob.arooa.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class EtcUtilsTest {

    @Test
    void whenDifferentMethodsThenPropertyDerived() {

        assertThat(EtcUtils.propertyFromMethodName("setFoo").orElseThrow(),
                is("foo"));
        assertThat(EtcUtils.propertyFromMethodName("getFoo").orElseThrow(),
                is("foo"));
        assertThat(EtcUtils.propertyFromMethodName("isFoo").orElseThrow(),
                is("foo"));
        assertThat(EtcUtils.propertyFromMethodName("goFoo").isEmpty(),
                is(true));
        assertThat(EtcUtils.propertyFromMethodName("getURLto").orElseThrow(),
                is("URLto"));
        assertThat(EtcUtils.propertyFromMethodName("set").orElseThrow(),
                is(""));
    }

    @Test
    void listToStringWithManyElements() {

        List<Integer> l = new ArrayList<>();

        assertThat(EtcUtils.toString(l, l.size()), is("[]"));

        l.add(1);

        assertThat(EtcUtils.toString(l, l.size()), is("[1]"));

        l.add(2);

        assertThat(EtcUtils.toString(l, l.size()), is("[1, 2]"));

        l.add(3);
        l.add(4);
        l.add(5);

        assertThat(EtcUtils.toString(l, l.size()), is("[1, 2, 3, 4, 5]"));
        assertThat(EtcUtils.toString(l, l.size()), is("[1, 2, 3, 4, 5]"));

        l.add(6);

        assertThat(EtcUtils.toString(l, l.size()), is("[1, 2, 3, 4, 5, ...(and 1 more)]"));

        l.add(7);

        assertThat(EtcUtils.toString(l, l.size()), is("[1, 2, 3, 4, 5, ...(and 2 more)]"));
    }

    @Test
    void listToStringWithLongElements() {

        List<String> l = new ArrayList<>();
        l.add("abcdef");

        assertThat(EtcUtils.toString(l, l.size(), 5, 3), is("[abc...]"));

        l.add("xyz");

        assertThat(EtcUtils.toString(l, l.size(), 5, 3), is("[abc..., (and 1 more)]"));

    }
}