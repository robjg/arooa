package org.oddjob.arooa.convert.doc;

import org.oddjob.arooa.beandocs.WriteableConversionDoc;
import org.oddjob.arooa.convert.ClassOrMethod;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.Joker;

import java.lang.reflect.Type;

/**
 * Provides {@link WriteableConversionDoc} for a conversion.
 */
public class ConversionDocProvider implements ConversionItemProvider<WriteableConversionDoc> {

    @Override
    public WriteableConversionDoc forConvertlet(Type from, Type to, Convertlet<?, ?> convertlet) {

        WriteableConversionDoc doc = new WriteableConversionDoc();
        doc.setTypeOrMethod(ClassOrMethod.ofClass(convertlet.getClass()).getName());
        doc.setFromType(from.getTypeName());
        doc.setToType(to.getTypeName());

        return doc;
    }

    @Override
    public WriteableConversionDoc forJoker(Type from, Joker<?> joker) {

        WriteableConversionDoc doc = new WriteableConversionDoc();
        doc.setTypeOrMethod(ClassOrMethod.ofClass(joker.getClass()).getName());
        doc.setFromType(from.getTypeName());
        doc.setToType(null);

        return doc;
    }
}
