package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.convert.doc.ConversionItemAccess;

/**
 * Writable {@link ConversionDocs}.
 */
public class WriteableConversionDocs implements ConversionDocs {

    private final ConversionItemAccess<WriteableConversionDoc> itemAccess;

    public WriteableConversionDocs(ConversionItemAccess<WriteableConversionDoc> itemAccess) {
        this.itemAccess = itemAccess;
    }

    @Override
    public WriteableConversionDoc conversionDocumentedByType(String typeName) {

        return itemAccess.getForType(typeName);
    }

    @Override
    public WriteableConversionDoc conversionDocumentedByMethod(String typeName, String methodName) {

        return itemAccess.getForMethod(typeName, methodName);
    }

    @Override
    public boolean containsDocumentedByType(String typeName) {

        return itemAccess.containsForType(typeName);
    }

    @Override
    public ConversionDoc[] getConversionDocs() {

        return itemAccess.getAll().toArray(ConversionDoc[]::new);
    }
}
