package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Convert a Nashorn Javascript function into a Java Function.
 */
public class NashornConvertlets implements ConversionProvider {

    private static final Logger logger = LoggerFactory.getLogger(NashornConvertlets.class);

    public static final String SCRIPT_OBJECT_MIRROR_8 = "jdk.nashorn.api.scripting.ScriptObjectMirror";

    public static final String SCRIPT_OBJECT_MIRROR_9 = "org.openjdk.nashorn.api.scripting.ScriptObjectMirror";

    @Override
    public void registerWith(ConversionRegistry registry) {

        Class<?> cl;
        try {
            cl = Class.forName(SCRIPT_OBJECT_MIRROR_9);
            logger.debug("Using Jdk 9+ Nashorn Scripting {}", cl.getName());
        } catch (ClassNotFoundException e) {
            try {
                cl = Class.forName(SCRIPT_OBJECT_MIRROR_8);
                logger.debug("Using Jdk 8 Nashorn Scripting {}", cl.getName());
            } catch (ClassNotFoundException ex) {
                logger.debug("No Noashorn Scripting found");
                return;
            }
        }

        Method m;
        try {
            m = cl.getMethod("call", Object.class, Object[].class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Can't find call method on {" + cl.getName() +
                    "}. This shouldn't happen.", e);
        }

        Class<?> finalCl = cl;
        registry.register(cl, Function.class,
                from -> arg -> {
                    try {
                        if (arg == null) {
                            return m.invoke(from, null, new Object[]{null});
                        } else {
                            return m.invoke(from, null, new Object[]{arg});
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalArgumentException("Failed invoking call on " + finalCl.getName(), e);
                    }
                });
    }
}
