package org.oddjob.arooa.beandocs;


/**
 * Something that is able to provide {@link ConversionDoc}. This is analogous to {@link ArooaDoc}.
 */
public interface ConversionDocs {

    ConversionDoc[] getConversionDocs();

    ConversionDoc[] getConversionDocsFrom(String typeNameFrom);

    ConversionDoc conversionDocumentedByType(String typeName);

    ConversionDoc conversionDocumentedByMethod(String typeName, String methodName);

    boolean containsDocumentedByType(String typeName);

}
