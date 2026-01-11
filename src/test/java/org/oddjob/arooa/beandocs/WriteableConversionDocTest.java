package org.oddjob.arooa.beandocs;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

class WriteableConversionDocTest {

    @Test
    void gettersAndSetters() {

        WriteableConversionDoc test = new WriteableConversionDoc();
        test.setTypeOrMethod("MyConversion");
        test.setFromType("FromSomething");
        test.setToType("ToSomething");

        List<BeanDocElement> firstSentence = new ArrayList<>();
        List<BeanDocElement> allText = new ArrayList<>();

        test.setFirstSentence(firstSentence);
        test.setAllText(allText);

        assertThat(test.getTypeOrMethod(), is("MyConversion"));
        assertThat(test.getFromType(), is("FromSomething"));
        assertThat(test.getToType(), is("ToSomething"));
        assertThat(test.getFirstSentence(), sameInstance(firstSentence));
        assertThat(test.getAllText(), sameInstance(allText));
    }
}