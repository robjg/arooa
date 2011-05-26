package org.oddjob.arooa.life;

import org.oddjob.arooa.parsing.ArooaContext;

public interface ComponentProxyResolver  {

	public Object resolve(Object object, ArooaContext parentContext);
	
	public Object restore(Object proxy, ArooaContext parentContext);
}
