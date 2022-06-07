package org.oddjob.arooa.deploy;

import org.junit.Test;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SupportedBeanDescriptorProviderTest {

    public static class Apple {

        public void setRotten(boolean rotten) {

        }
    }

    @Test
    public void testWithBeanDefinition() {

        BeanDefinitionBean definition = new BeanDefinitionBean();
        definition.setClassName(Apple.class.getName());

        ArooaBeanDescriptor result = SupportedBeanDescriptorProvider.withBeanDefinition(definition)
                .getBeanDescriptor(
                        new SimpleArooaClass(Apple.class),
                        new BeanUtilsPropertyAccessor());

        assertThat(result.getConfiguredHow("rotten"), is(ConfiguredHow.ATTRIBUTE));
    }
}