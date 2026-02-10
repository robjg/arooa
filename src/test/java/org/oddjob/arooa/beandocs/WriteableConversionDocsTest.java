package org.oddjob.arooa.beandocs;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.doc.ConversionItemAccess;
import org.oddjob.arooa.convert.doc.MethodIdentifier;
import org.oddjob.arooa.convert.doc.TypeIdentifier;

import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WriteableConversionDocsTest {

    @Test
    void addAndGet() throws NoSuchMethodException {

        Method method = WriteableConversionDocsTest.class.getDeclaredMethod("addAndGet");

        WriteableConversionDoc item = new WriteableConversionDoc();

        ConversionItemAccess<WriteableConversionDoc> itemAccess = mock(ConversionItemAccess.class);
        when(itemAccess.containsForType(TypeIdentifier.ofClass(WriteableConversionDocsTest.class)))
                .thenReturn(true);
        when(itemAccess.getForMethod(MethodIdentifier.ofMethod(method)))
                .thenReturn(item);
        when(itemAccess.getAll())
                .thenReturn(List.of(item));

        WriteableConversionDocs test = new WriteableConversionDocs(itemAccess);

        assertThat(test.containsDocumentedByType(TypeIdentifier.ofClass(WriteableConversionDocsTest.class)),
                is(true));

        assertThat(test.conversionDocumentedByType(TypeIdentifier.ofClass(WriteableConversionDocsTest.class)),
                nullValue());

        WriteableConversionDoc doc = test.conversionDocumentedByMethod(MethodIdentifier.ofMethod(method));

        assertThat(doc, sameInstance(item));

        assertThat(test.getConversionDocs(),
                is(new ConversionDoc[]{doc}));
    }
}