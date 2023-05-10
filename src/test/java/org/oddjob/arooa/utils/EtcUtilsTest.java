package org.oddjob.arooa.utils;

import org.junit.jupiter.api.Test;

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
}