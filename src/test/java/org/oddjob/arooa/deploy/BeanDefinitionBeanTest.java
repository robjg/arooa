package org.oddjob.arooa.deploy;

import org.junit.Assert;
import org.junit.Test;

public class BeanDefinitionBeanTest extends Assert {

    public static class Apple {

        public void setDescription(String text) {

        }
    }

    @Test
    public void testIsBeanDescriptor() {

        BeanDefinitionBean test = new BeanDefinitionBean();
        test.setClassName(Apple.class.getName());
        test.setElement("apple");
        test.setDesignFactory("AppleDesignFactory");

        assertFalse(test.isArooaBeanDescriptor());
    }


}
