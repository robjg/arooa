package org.oddjob.arooa.beandocs;

import java.util.List;

/**
 * Something that provides an archive of {@link ConversionDoc}. This is analogous to {@link BeanDocArchive}.
 * It is this that is used to provide that documentation as apposed to {@link ConversionDocs} which
 * is intended to be their representation during and after creation.
 */
public interface ConversionArchive {

    List<ConversionDoc> conversionDocFor(String typeName);

    List<ConversionDoc> allConversionDoc();

}
