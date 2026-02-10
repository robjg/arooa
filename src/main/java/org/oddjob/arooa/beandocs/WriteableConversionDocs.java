package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.convert.doc.ConversionItemAccess;
import org.oddjob.arooa.convert.doc.MethodIdentifier;
import org.oddjob.arooa.convert.doc.TypeIdentifier;

/**
 * Writable {@link ConversionDocs}.
 */
public class WriteableConversionDocs implements ConversionDocs {

    private final ConversionItemAccess<WriteableConversionDoc> itemAccess;

    public WriteableConversionDocs(ConversionItemAccess<WriteableConversionDoc> itemAccess) {
        this.itemAccess = itemAccess;
    }

    @Override
    public WriteableConversionDoc conversionDocumentedByType(TypeIdentifier typeIdentifier) {

        return itemAccess.getForType(typeIdentifier);
    }

    @Override
    public WriteableConversionDoc conversionDocumentedByMethod(MethodIdentifier methodIdentifier) {

        return itemAccess.getForMethod(methodIdentifier);
    }

    @Override
    public boolean containsDocumentedByType(TypeIdentifier typeIdentifier) {

        return itemAccess.containsForType(typeIdentifier);
    }

    @Override
    public ConversionDoc[] getConversionDocs() {

        return itemAccess.getAll().toArray(ConversionDoc[]::new);
    }
}
