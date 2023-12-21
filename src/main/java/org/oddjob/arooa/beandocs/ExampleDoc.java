package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.List;

public interface ExampleDoc {

	List<BeanDocElement> getFirstSentence();

	List<BeanDocElement> getAllText();
}
