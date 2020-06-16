package org.oddjob.arooa.json;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.parsing.ArooaElement;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A generic form of a Configuration. This is an intermediate configuration particularly
 * suited to moving to and from a JSON configuration.
 * <p/>
 * Child configurations are grouped by names. Names would be the property names in
 * the typical Arooa configuration:
 */
public interface ConfigurationTree  {

    /**
     * Get the element of this configuration.
     *
     * @return An Element. Never Null.
     */
    ArooaElement getElement();

    /**
     * Get the element text, if there is any.
     *
     * @return Optional Text.
     */
    Optional<String> getText();

    /**
     * Get the names of child configurations.
     *
     * @return A set of names. May be empty but never null.
     */
    Set<String> getChildNames();

    /**
     * Get the child configuration(s) for a name.
     *
     * @param name The name of the child(ren)
     * @return If the name exists then a list of at least one configuration otherwise null.
     */
    List<ConfigurationTree> getChildConfigurations(String name);

    /**
     * Map to an ArooaConfiguration that can be parsed with an {@link org.oddjob.arooa.ArooaParser}.
     *
     * @param saveMethod Provide a way of saving changes to the configuration back.
     *
     * @return A valid {@link ArooaConfiguration}.
     */
    ArooaConfiguration toConfiguration(SaveOperation saveMethod);

}
