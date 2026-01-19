package org.oddjob.arooa.beandocs;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.doc.ConversionItemAccess;

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
        when(itemAccess.containsForType(WriteableConversionDocsTest.class.getTypeName()))
                .thenReturn(true);
        when(itemAccess.getForMethod(WriteableConversionDocsTest.class.getTypeName(), "addAndGet"))
                .thenReturn(item);
        when(itemAccess.getAll())
                .thenReturn(List.of(item));

        WriteableConversionDocs test = new WriteableConversionDocs(itemAccess);

        assertThat(test.containsDocumentedByType(WriteableConversionDocsTest.class.getTypeName()),
                is(true));

        assertThat(test.conversionDocumentedByType(WriteableConversionDocsTest.class.getTypeName()),
                nullValue());

        WriteableConversionDoc doc = test.conversionDocumentedByMethod(WriteableConversionDocsTest.class.getTypeName(),
                method.getName());

        assertThat(doc, sameInstance(item));

        assertThat(test.getConversionDocs(),
                is(new ConversionDoc[]{doc}));
    }
}