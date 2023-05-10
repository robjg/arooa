package org.oddjob.arooa.utils;

import java.beans.Introspector;
import java.util.Optional;

/**
 * General purpose methods without some category. Methods in this class are likely to
 * move to another class when there are enough to create a separate utility class of
 * like methods.
 */
public class EtcUtils {

    /**
     * Extract the property name from the method name, if the method name matches
     * a getter or setter pattern as specified by
     *  <a href="http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html">JavaBeans&trade; specification</a>.
     *  Except that an empty property name is supported as BeanUtils does this.
     *
     * @param methodName a method name
     * @return An Optional of the property name which is empty if the method name
     * does not follow a Java Beans pattern.
     */
    public static Optional<String> propertyFromMethodName(String methodName) {

        int start;
        if (methodName.startsWith("set")) {
            start = 3;
        }
        else if (methodName.startsWith("get")) {
            start = 3;
        }
        else if (methodName.startsWith("is")) {
            start = 2;
        }
        else {
            return Optional.empty();
        }

        String property = methodName.substring(start);

        if (property.length() == 0) {
            return Optional.of("");
        }

        return Optional.of(Introspector.decapitalize(property));
    }
}
