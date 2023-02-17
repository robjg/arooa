package org.oddjob.arooa.types;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;

class ValueFactoryTest extends Assert {

    static class LongFactory implements ValueFactory<Long> {

        @Override
        public Long toValue() throws ArooaConversionException {
            return 42L;
        }
    }


	@Test
    void testOnwardConversion() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConverter converter = new DefaultConverter();

        Double result = converter.convert(new LongFactory(), Double.class);

        MatcherAssert.assertThat(result,  Matchers.closeTo(42.0, 0.01));
    }

	@Test
	void whenConversionToObjectThenConversionUsed() throws NoConversionAvailableException, ConversionFailedException {

		DefaultConverter converter = new DefaultConverter();

		Object result = converter.convert(new LongFactory(), Object.class);

		MatcherAssert.assertThat((Long) result,  Matchers.is(42L));
	}
}
