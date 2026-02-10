package org.oddjob.arooa.convert.doc;

/**
 * An Identifier for a class or other type for indexing conversion docs.
 */
public abstract class TypeIdentifier implements ElementIdentifier {

    /**
     * Provide an Identifier for the given class or null if the class is a Lambda as it can't
     * be documented.
     *
     * @param cl The class.
     * @return The Type Identifier or null.
     */
    public static TypeIdentifier ofClass(Class<?> cl) {
        if (cl.getCanonicalName() == null) {
            return null;
        }
        return new ClassTypeIdentifier(cl);
    }

    public abstract String getClassName();

    @Override
    public TypeIdentifier getTypeIdentifier() {
        return this;
    }

    @Override
    public int hashCode() {
        return getClassName().hashCode();
    }

    @Override
    public boolean equals(Object o) {

        return (this == o) || (o instanceof TypeIdentifier t && t.getClassName().equals(getClassName()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + getName() + '\'' +
                '}';
    }

    static private class ClassTypeIdentifier extends TypeIdentifier {

        private final Class<?> cl;

        ClassTypeIdentifier(Class<?> cl) {
            this.cl = cl;
        }

        @Override
        public String getName() {
            return cl.getCanonicalName();
        }

        @Override
        public String getClassName() {
            return cl.getName();
        }
    }
}
