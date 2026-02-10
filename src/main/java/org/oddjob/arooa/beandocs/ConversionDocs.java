package org.oddjob.arooa.beandocs;


import org.oddjob.arooa.convert.doc.MethodIdentifier;
import org.oddjob.arooa.convert.doc.TypeIdentifier;

/**
 * Something that is able to provide {@link ConversionDoc}. This is analogous to {@link ArooaDoc}.
 */
public interface ConversionDocs {

    ConversionDoc[] getConversionDocs();

    ConversionDoc conversionDocumentedByType(TypeIdentifier typeIdentifier);

    ConversionDoc conversionDocumentedByMethod(MethodIdentifier methodIdentifier);

    boolean containsDocumentedByType(TypeIdentifier typeIdentifier);

}
