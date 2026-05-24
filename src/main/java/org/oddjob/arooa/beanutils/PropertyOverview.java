package org.oddjob.arooa.beanutils;

import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.MethodUtils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Wrapper for a {@code PropertyDescriptor} that supports generic type properties.
 */
public abstract class PropertyOverview {

    private volatile Type propertyType;

    private volatile Boolean readable;

    private volatile Boolean writable;

    protected abstract PropertyDescriptor getDescriptor();

    protected abstract Method getReadMethod();

    protected abstract Method getWriteMethod();

    protected abstract int writePropertyTypeIndex();

    /**
     * Get the property type, using generic types when available.
     * For indexed/mapped properties, returns the element/value type.
     *
     * @return The property type.
     */
    public Type getPropertyType() {
        if (propertyType == null) {
            Method method = getReadMethod();
            if (method != null) {
                propertyType = method.getGenericReturnType();
            }
            else {
                method = getWriteMethod();
                if (method != null) {
                    propertyType = method.getGenericParameterTypes()[writePropertyTypeIndex()];
                }
                else {
                    throw new IllegalStateException("No read or write method for property " + getDescriptor().getName());
                }
            }
        }
        return propertyType;
    }

    /**
     * Check if this property has a readable accessor.
     *
     * @return true if readable.
     */
    public boolean isReadable() {
        if (readable == null) {
            Method m = getReadMethod();
            m = MethodUtils.getAccessibleMethod(m);
            readable = m != null;
        }
        return readable;
    }


    /**
     * Check if this property has a writable accessor.
     *
     * @return true if writable.
     */
    public boolean isWritable() {
        if (writable == null) {
            Method m = getWriteMethod();
            m = MethodUtils.getAccessibleMethod(m);
            writable = m != null;
        }
        return writable;
    }

    /**
     * Check if this property is indexed.
     *
     * @return true if indexed.
     */
    public abstract boolean isIndexed();

    /**
     * Check if this property is mapped.
     *
     * @return true if mapped.
     */
    public abstract boolean isMapped();

    /**
     * Create a PropertyOverview for a simple (non-indexed, non-mapped) property.
     *
     * @param descriptor The property descriptor.
     * @return A PropertyOverview wrapping the descriptor.
     */
    public static PropertyOverview ofSimple(PropertyDescriptor descriptor) {
        return new SimplePropertyOverview(descriptor);
    }

    /**
     * Create a PropertyOverview for an indexed property.
     *
     * @param descriptor The indexed property descriptor.
     * @return A PropertyOverview wrapping the descriptor.
     */
    public static PropertyOverview ofIndexed(IndexedPropertyDescriptor descriptor) {
        return new IndexedPropertyOverview(descriptor);
    }

    /**
     * Create a PropertyOverview for a mapped property.
     *
     * @param descriptor The mapped property descriptor.
     * @return A PropertyOverview wrapping the descriptor.
     */
    public static PropertyOverview ofMapped(MappedPropertyDescriptor descriptor) {
        return new MappedPropertyOverview(descriptor);
    }

    static class SimplePropertyOverview extends PropertyOverview {

        private final PropertyDescriptor descriptor;

        public SimplePropertyOverview(PropertyDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public PropertyDescriptor getDescriptor() {
            return descriptor;
        }

        @Override
        protected Method getReadMethod() {
            return descriptor.getReadMethod();
        }

        @Override
        protected Method getWriteMethod() {
            return descriptor.getWriteMethod();
        }

        @Override
        protected int writePropertyTypeIndex() {
            return 0;
        }

        @Override
        public boolean isIndexed() {
            return false;
        }

        @Override
        public boolean isMapped() {
            return false;
        }
    }

    static class IndexedPropertyOverview extends PropertyOverview {

        private final IndexedPropertyDescriptor descriptor;

        public IndexedPropertyOverview(IndexedPropertyDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public PropertyDescriptor getDescriptor() {
            return descriptor;
        }

        @Override
        protected Method getReadMethod() {
            return descriptor.getIndexedReadMethod();
        }

        @Override
        protected Method getWriteMethod() {
            return descriptor.getIndexedWriteMethod();
        }

        @Override
        protected int writePropertyTypeIndex() {
            return 1;
        }

        @Override
        public boolean isIndexed() {
            return true;
        }

        @Override
        public boolean isMapped() {
            return false;
        }

    }

    static class MappedPropertyOverview extends PropertyOverview {

        private final MappedPropertyDescriptor descriptor;

        public MappedPropertyOverview(MappedPropertyDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public PropertyDescriptor getDescriptor() {
            return descriptor;
        }

        @Override
        protected Method getReadMethod() {
            return descriptor.getMappedReadMethod();
        }

        @Override
        protected Method getWriteMethod() {
            return descriptor.getMappedWriteMethod();
        }

        @Override
        protected int writePropertyTypeIndex() {
            return 1;
        }

        @Override
        public boolean isIndexed() {
            return false;
        }

        @Override
        public boolean isMapped() {
            return true;
        }

    }
}
