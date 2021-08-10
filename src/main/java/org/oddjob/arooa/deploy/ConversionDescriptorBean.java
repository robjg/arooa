package org.oddjob.arooa.deploy;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionProviderFactory;
import org.oddjob.arooa.convert.ReflectionConversionProvider;
import org.oddjob.arooa.utils.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @oddjob.description
 * Provide a Bean for use in an {@link ArooaDescriptorBean} that provides conversions.
 *
 * @oddjob.example Creating Gremlins
 * <p>
 * {@oddjob.xml.resource org/oddjob/arooa/types/ReflectionConversionMain.xml}
 * {@oddjob.xml.resource org/oddjob/arooa/types/ReflectionConversionExample.xml}
 */
public class ConversionDescriptorBean implements ConversionProviderFactory {

    /**
     * @oddjob.property
     * @oddjob.description The class name of the conversion. If a method is provided then
     * this is the name of the class that has that method and the conversion will be from
     * objects of that class to the return type of the method. If method is provided then
     * this class name must refer to an {@link ConversionProvider}.
     * @oddjob.required yes.
     */
    private String className;

    /**
     * @oddjob.property
     * @oddjob.description The name of the method that provides the conversion if the conversion
     * is not an {@link ConversionProvider}. The conversion will be provided via reflection
     * at runtime using this method. The return type of this method is used to register the
     * conversion 'to' class.
     * @oddjob.required No.
     */
    private String methodName;

    @Override
    public ConversionProvider createConversionProvider(ClassLoader classLoader) {

        try {
            Class<?> providerClass = ClassUtils.classFor(className, classLoader);
            if (methodName == null) {
                Object provider = providerClass
                        .getDeclaredConstructor().newInstance();

                return (ConversionProvider) provider;
            }
            else {
                Method method = providerClass.getMethod(methodName);
                return new ReflectionConversionProvider(providerClass, method)
                        .createConversionProvider(classLoader);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "ConversionDescriptorBean{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
