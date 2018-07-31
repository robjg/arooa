package org.oddjob.arooa.design;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.etc.UnknownComponent;
import org.oddjob.arooa.design.etc.UnknownInstance;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;
import org.oddjob.arooa.runtime.RuntimeListenerAdapter;

abstract class XMLFirstHandler implements ArooaHandler {

	private static final Logger logger = LoggerFactory.getLogger(PropertyContext.class);
	
	private DesignInstance xmlDesign;

	private DesignInstance goodDesign;

	private final ArooaHandler unknownHandler = new ArooaHandler() {

		public ArooaContext onStartElement(ArooaElement element,
				final ArooaContext parentContext) throws ArooaException {

			if (parentContext.getArooaType() == ArooaType.COMPONENT) {
				xmlDesign = new UnknownComponent(element, parentContext);
			} else {
				xmlDesign = new UnknownInstance(element, parentContext);
			}

			RuntimeListener listener = new RuntimeListenerAdapter() {

				@Override
				public void beforeInit(RuntimeEvent event)
						throws ArooaException {
					onBeforeInit();
				}
				
				@Override
				public void afterInit(RuntimeEvent event) throws ArooaException {

					useHandler = designHandler;

					int index = parentContext.getConfigurationNode().indexOf(
							xmlDesign.getArooaContext().getConfigurationNode());

					if (index < 0) {
						throw new IllegalStateException(
								"Configuration not child of parent.");
					}

					parentContext.getConfigurationNode().removeChild(index);
					parentContext.getConfigurationNode().setInsertPosition(
							index);

					DesignInstance useDesign = xmlDesign;

					try {
						xmlDesign.getArooaContext().getConfigurationNode()
								.parse(parentContext);
						useDesign = goodDesign;
					} catch (Exception e) {
						logger.error("Failed creating Design from XML: " + 
								e.getMessage(), e);
						parentContext.getConfigurationNode().insertChild(
								xmlDesign.getArooaContext()
										.getConfigurationNode());
					} finally {
						parentContext.getConfigurationNode().setInsertPosition(
								-1);
						useHandler = unknownHandler;
						onAfterInit();
					}

					setDesign(index, useDesign);
				}
			};

			xmlDesign.getArooaContext().getRuntime().addRuntimeListener(
					listener);

			return xmlDesign.getArooaContext();
		}
	};

	private final ArooaHandler designHandler = new ArooaHandler() {

		public ArooaContext onStartElement(ArooaElement element,
				ArooaContext parentContext) throws ArooaConfigurationException {

			goodDesign = goodDesign(element, parentContext);

			return goodDesign.getArooaContext();
		}
	};

	private ArooaHandler useHandler = unknownHandler;

	public ArooaContext onStartElement(ArooaElement element,
			ArooaContext parentContext) 
	throws ArooaConfigurationException {
		return useHandler.onStartElement(element, parentContext);
	}

	abstract DesignInstance goodDesign(
			ArooaElement element, ArooaContext parentContext)
	throws ArooaPropertyException;
	
	void onBeforeInit() {}
	
	void onAfterInit() {}
	
	abstract void setDesign(int index, DesignInstance design);
	
}
