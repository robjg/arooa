package org.oddjob.arooa.design.layout;

import org.oddjob.arooa.ArooaConfiguration;

import java.util.Map;

/**
 * Results of {@link DesignToLayoutConfig}.
 */
public class DesignableInfo {

    private final ArooaConfiguration designConfiguration;

    private final Map<String, String[]> propertyOptions;

    public DesignableInfo(ArooaConfiguration designConfiguration, Map<String, String[]> propertyOptions) {
        this.designConfiguration = designConfiguration;
        this.propertyOptions = propertyOptions;
    }

    public ArooaConfiguration getDesignConfiguration() {
        return designConfiguration;
    }

    public Map<String, String[]> getPropertyOptions() {
        return propertyOptions;
    }
}
