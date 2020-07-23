package org.oddjob.arooa.parsing;

import org.junit.Test;
import org.oddjob.arooa.runtime.ConfigurationNodeEvent;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;
import org.oddjob.arooa.runtime.ModificationRefusedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SimpleParseContextTest {


    @Test
    public void testActions() {

        ArooaElement a = new ArooaElement("a");

        ArooaElement b = new ArooaElement("b");

        ArooaElement c = new ArooaElement("c");

        List<String> actionsPerformed = new ArrayList<>(20);

        AtomicReference<SimpleParseContext.ActionFunction> secondActions = new AtomicReference<>();

        SimpleParseContext.ActionFunction firstActions =
                element -> {
                    actionsPerformed.add("First on " + element);
                    return SimpleParseContext.actions()
                            .withInitAction(() -> actionsPerformed.add("init " + element))
                            .withChildActions(secondActions.get())
                            .create();
                };

        secondActions.set(
                element -> {
                    actionsPerformed.add("Second on " + element);
                    return SimpleParseContext.actions()
                            .withChildActions(firstActions)
                            .withTextConsumer(text -> actionsPerformed.add("Text " + text + " on " + element))
                            .withDestroyAction(() -> actionsPerformed.add("destroy " + element))
                            .create();
                });

        SimpleParseContext rootContext = SimpleParseContext.createRootContext()
                .withActions(firstActions)
                .andNoPrefixMappings();

        ParseHandle<SimpleParseContext> aHandle = rootContext.getElementHandler()
                .onStartElement(a, rootContext);

        ParseHandle<SimpleParseContext> bHandle = aHandle.getContext().getElementHandler()
                .onStartElement(b, aHandle.getContext());

        bHandle.addText("Boo");

        ParseHandle<SimpleParseContext> cHandle = bHandle.getContext().getElementHandler()
                .onStartElement(c, bHandle.getContext());

        cHandle.init();

        bHandle.init();

        aHandle.init();

        cHandle.getContext().destroy();

        bHandle.getContext().destroy();

        aHandle.getContext().destroy();

        System.out.println(actionsPerformed);

        assertThat(actionsPerformed, contains(
                "First on a",
                "Second on b",
                "First on c",
                "init c",
                "Text Boo on b",
                "init a",
                "destroy b"
        ));

    }

    @Test
    public void testPrefixMappings() throws URISyntaxException {

        SimplePrefixMappings prefixMappings = new SimplePrefixMappings();

        SimpleParseContext test = SimpleParseContext.createRootContext()
                .withNoActions()
                .andPrefixMappings(prefixMappings);

        URI uri = new URI("test:foo");

        ArooaElement a = new ArooaElement(uri, "a");

        ParseHandle<SimpleParseContext> aHandle = test.getElementHandler()
                .onStartElement(a, test);

        aHandle.getContext().getPrefixMappings().put("foo", uri);

        assertThat(prefixMappings.getUriFor("foo"),
                is(uri));
    }

    @Test
    public void testDestroy() {

        ArooaElement a = new ArooaElement("a");

        ArooaElement b = new ArooaElement("b");

        ArooaElement c = new ArooaElement("c");

        SimpleParseContext rootContext = SimpleParseContext.createRootContext()
                .withNoActions()
                .andNoPrefixMappings();

        ParseHandle<SimpleParseContext> aHandle = rootContext.getElementHandler()
                .onStartElement(a, rootContext);

        ParseHandle<SimpleParseContext> bHandle = aHandle.getContext().getElementHandler()
                .onStartElement(b, aHandle.getContext());

        List<String> actions = new ArrayList<>();

        aHandle.getContext().getConfigurationNode().addNodeListener(
                new ConfigurationNodeListener<SimpleParseContext>() {
                    @Override
                    public void insertRequest(ConfigurationNodeEvent<SimpleParseContext> nodeEvent) throws ModificationRefusedException {
                    }

                    @Override
                    public void removalRequest(ConfigurationNodeEvent<SimpleParseContext> nodeEvent) throws ModificationRefusedException {
                    }

                    @Override
                    public void childInserted(ConfigurationNodeEvent<SimpleParseContext> nodeEvent) {
                        actions.add("insert");
                    }

                    @Override
                    public void childRemoved(ConfigurationNodeEvent<SimpleParseContext> nodeEvent) {
                        actions.add("remove");
                    }
                }
        );

        assertThat(actions, empty());

        bHandle.init();

        assertThat(actions, contains("insert"));

        aHandle.init();

        bHandle.getContext().destroy();

        assertThat(actions, contains("insert", "remove"));

        aHandle.getContext().destroy();
    }

}