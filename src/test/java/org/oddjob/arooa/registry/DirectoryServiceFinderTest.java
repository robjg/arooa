package org.oddjob.arooa.registry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class DirectoryServiceFinderTest extends Assert {

    private static final Logger logger =
            LoggerFactory.getLogger(DirectoryServiceFinderTest.class);

    @Rule
    public TestName name = new TestName();

    public String getName() {
        return name.getMethodName();
    }

    interface FruitService {
    }

    @Before
    public void setUp() throws Exception {


        logger.info("-----------------------  {}#{}  -----------------------", getClass().getName(), getName());
    }

    private static class TheServices implements Services {

        private final FruitService fruitService;

        public TheServices(FruitService fruitService) {
            this.fruitService = fruitService;
        }

        @Override
        public Object getService(String serviceName)
                throws IllegalArgumentException {
            if ("FRUIT".equals(serviceName)) {
                return fruitService;
            } else {
                throw new IllegalArgumentException(serviceName);
            }
        }

        @Override
        public String serviceNameFor(Type theClass, String flavour) {
            if (theClass == FruitService.class) {
                return "FRUIT";
            }
            return null;
        }
    }

    static class AppleService implements ServiceProvider, FruitService {

        @Override
        public Services getServices() {
            return new TheServices(this);
        }
    }


    static class OrangeService implements ServiceProvider, FruitService {

        @Override
        public Services getServices() {
            return new TheServices(this);
        }
    }

    @Test
    public void testTwoMatches() {

        SimpleBeanRegistry directory = new SimpleBeanRegistry();

        AppleService appleService = new AppleService();

        OrangeService orangeService = new OrangeService();


        directory.register("apples", appleService);

        directory.register("oranges", orangeService);

        DirectoryServiceFinder test = new DirectoryServiceFinder(
                directory);

        Object result = test.find(FruitService.class, null);

        assertEquals(appleService, result);
    }
}
