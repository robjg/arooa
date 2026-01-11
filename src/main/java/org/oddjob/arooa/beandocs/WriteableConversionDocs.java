package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.convert.ClassOrMethod;

import java.util.*;

/**
 * Writable {@link ConversionDocs}.
 */
public class WriteableConversionDocs implements ConversionDocs {

    private final Set<String> typeNames = new HashSet<>();

    private final Map<ClassOrMethod, WriteableConversionDoc> conversionDocByDocumented = new HashMap<>();

    private final Map<String, List<WriteableConversionDoc>> conversionDocsByFrom = new HashMap<>();

    @Override
    public WriteableConversionDoc conversionDocumentedByType(String typeName) {

        return conversionDocByDocumented.get(ClassOrMethod.ofCanonicalClassName(typeName));
    }

    @Override
    public WriteableConversionDoc conversionDocumentedByMethod(String typeName, String methodName) {

        return conversionDocByDocumented.get(ClassOrMethod.ofTypeAndMethodNames(typeName, methodName));
    }

    @Override
    public boolean containsDocumentedByType(String typeName) {
        return typeNames.contains(typeName);
    }

    public List<ConversionDoc> conversionDocsFrom(String typeName) {

        return new ArrayList<>(conversionDocsByFrom.get(typeName));
    }

    @Override
    public ConversionDoc[] getConversionDocs() {
        return conversionDocsByFrom.keySet().stream()
                .flatMap(typeName -> conversionDocsByFrom.get(typeName).stream())
                .toArray(ConversionDoc[]::new);
    }

    @Override
    public ConversionDoc[] getConversionDocsFrom(String typeNameFrom) {
        return Optional.ofNullable(conversionDocsByFrom.get(typeNameFrom))
                .map(l -> l.reversed().toArray(new ConversionDoc[0]))
                .orElse(new ConversionDoc[0]);
    }

    public void add(ClassOrMethod documentedBy, WriteableConversionDoc conversionDoc) {

        if (documentedBy != null) {
            typeNames.add(documentedBy.getCanonicalClassName());
            conversionDocByDocumented.put(documentedBy, conversionDoc);
        }

        String from = conversionDoc.getFromType();
        conversionDocsByFrom.computeIfAbsent(from, k -> new ArrayList<>()).add(conversionDoc);
    }
}
