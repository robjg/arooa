package org.oddjob.arooa.convert.doc;

import org.oddjob.arooa.beandocs.WriteableConversionDoc;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Provides {@link WriteableConversionDoc} for a conversion.
 */
public class ConversionDocProvider implements ConversionItemProvider<WriteableConversionDoc> {


    @Override
    public WriteableConversionDoc create(Type from, Type to, TypeIdentifier typeIdentifier) {

        WriteableConversionDoc doc = new WriteableConversionDoc();
        doc.setTypeOrMethod(Optional.ofNullable(typeIdentifier)
                .map(ElementIdentifier::getName).orElse(null));
        doc.setFromType(Optional.ofNullable(from)
                .map(Type::getTypeName).orElse(null));
        doc.setToType(Optional.ofNullable(to)
                .map(Type::getTypeName).orElse(null));

        return doc;
    }
}
