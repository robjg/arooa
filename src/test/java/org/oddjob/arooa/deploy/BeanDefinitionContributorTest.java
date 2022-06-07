package org.oddjob.arooa.deploy;


import org.junit.Test;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.life.SimpleArooaClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BeanDefinitionContributorTest {

    @Test
    public void testSimpleBeanDefinitionWithOneProperty() {

        BeanDescriptorBuilder builder = new BeanDescriptorBuilder(
                new SimpleArooaClass(Object.class));

        BeanDefinitionBean definition = new BeanDefinitionBean();
        definition.setProperties(0,
                new PropertyDefinitionBean("apple",
                        PropertyDefinitionBean.PropertyType.ELEMENT));
        definition.setProperties(1,
                new PropertyDefinitionBean("description",
                        PropertyDefinitionBean.PropertyType.TEXT));

        BeanDefinitionContributor contributor = new BeanDefinitionContributor();

        contributor.makeContribution(definition, builder);

        ArooaBeanDescriptor beanDescriptor = builder.build();

        assertThat(beanDescriptor.getConfiguredHow("apple"), is(ConfiguredHow.ELEMENT));
        assertThat(beanDescriptor.getConfiguredHow("description"), is(ConfiguredHow.TEXT));
    }
}