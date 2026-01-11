package org.oddjob.arooa.beandocs;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.ClassOrMethod;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class WriteableConversionDocsTest {

    @Test
    void addAndGet() throws NoSuchMethodException {

        Method method = WriteableConversionDocsTest.class.getDeclaredMethod("addAndGet");

        WriteableConversionDocs test = new WriteableConversionDocs();

        WriteableConversionDoc doc = new WriteableConversionDoc();
        doc.setTypeOrMethod(ClassOrMethod.ofMethod(method).getName());
        doc.setFromType(String.class.getTypeName());
        doc.setToType(Integer.class.getTypeName());

        test.add(ClassOrMethod.ofMethod(method), doc);

        assertThat(test.containsDocumentedByType(WriteableConversionDocsTest.class.getTypeName()),
                is(true));

        assertThat(test.conversionDocumentedByType(WriteableConversionDocsTest.class.getTypeName()),
                nullValue());

        assertThat(test.conversionDocumentedByMethod(WriteableConversionDocsTest.class.getTypeName(),
                        method.getName()),
                is(doc));

        assertThat(test.getConversionDocsFrom(String.class.getTypeName()),
                is(new ConversionDoc[] { doc }));
        assertThat(test.getConversionDocs(),
                is(new ConversionDoc[] { doc }));
    }
}