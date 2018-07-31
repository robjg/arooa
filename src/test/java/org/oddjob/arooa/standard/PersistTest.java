package org.oddjob.arooa.standard;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Assert;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.life.ComponentPersistException;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.life.MockComponentPersister;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.xml.XMLConfiguration;

public class PersistTest extends Assert {

	public static class Root {
		Fruit fruit;

		@ArooaComponent
		public void setFruit(Fruit fruit) {
			this.fruit = fruit;
		}
	}
	
	private interface Fruit {
		public String getColour();
		public void setColour(String colour);
	}
	
	public static class FruitImpl implements Fruit, Serializable {
		private static final long serialVersionUID = 2009010700L;
		
		private String colour;
		
		public String getColour() {
			return colour;
		}
		
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
	
	public static class FruitInvocationHandler implements InvocationHandler, Serializable {
		private static final long serialVersionUID = 2009010700L;
		
		FruitImpl impl;
		
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if ("getColour".equals(method.getName())) {
				return impl.getColour();
			}
			else if ("setColour".equals(method.getName())) {
				impl.setColour((String) args[0]);
				return null;
			}
			else {
				return method.invoke(this, args);
			}
		}
	}
	
	private class OurPersister extends MockComponentPersister {
		byte[] saved;
		private boolean closed;
		
		OurPersister() {
		}
		
		OurPersister(byte[] previous) {
			this.saved = previous;
		}
		
		@Override
		public Object restore(String id, ClassLoader classLoader,
				ArooaSession session) {
			assertEquals("fruit", id);
			
			if (saved == null) {
				return null;
			}
			
			try {
				ObjectInputStream ois = new ObjectInputStream(
						new ByteArrayInputStream(saved));
						
				return ois.readObject();
			}
			catch (Exception e) {
				throw new ArooaException(e);
			}
		}
		
		@Override
		public void persist(String id, Object proxy, ArooaSession session) {
			assertEquals("fruit", id);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			try {
				ObjectOutputStream oos = new ObjectOutputStream(out);
				oos.writeObject(proxy);
			} 
			catch (Exception e) {
				throw new ArooaException(e);				
			}
			saved = out.toByteArray();
		}
		
		@Override
		public void remove(String id, ArooaSession session) {
			if (closed) {
				return;
			}
			throw new RuntimeException("Unexpected.");
		}
		
		@Override
		public void close() {
			closed = true;
		}
	}
	
	private class OurSession extends StandardArooaSession {
		ComponentPersister persister;
		ComponentProxyResolver resolver;
		
		@Override
		public ComponentPersister getComponentPersister() {
			return persister;
		}
		
		@Override
		public ComponentProxyResolver getComponentProxyResolver() {
			return resolver;
		}
	}
	
	private ArooaConfiguration config = new XMLConfiguration("TEST", 
		"<ignored>" +
		"  <fruit>" +
		"	<bean class='" + FruitImpl.class.getName() + "' id='fruit'/>" +
		"  </fruit>" +
		"</ignored>");
	
   @Test
	public void testRoundTrip() throws ArooaParseException, ComponentPersistException {
		
		Root root = new Root();
		
		OurSession session = new OurSession();
		session.persister = new OurPersister();
		
		StandardArooaParser parser = new StandardArooaParser(root, session);
		parser.parse(config);

		ComponentPool components = session.getComponentPool();
		
		root.fruit.setColour("red");
		
		components.save(root.fruit);

		session.persister.close();
		
		components.remove(root.fruit);
		
		root.fruit = null;
		
		OurSession session2 = new OurSession();
		session2.persister = new OurPersister(
				((OurPersister) session.persister).saved);
		
		StandardArooaParser parser2 = new StandardArooaParser(root, session2);
		
		parser2.parse(config);
		
		assertEquals("red", root.fruit.getColour());

	}
	
	private class OurResolver implements ComponentProxyResolver {

		@Override
		public Object resolve(Object object, ArooaSession session) {
			if (object instanceof Root) {
				return null;
			}
			
			FruitInvocationHandler handler = new FruitInvocationHandler();
			handler.impl = (FruitImpl) object;
			
			return Proxy.newProxyInstance(
					getClass().getClassLoader(), 
					new Class<?>[] { Fruit.class}, 
					handler);
		}
		
		@Override
		public Object restore(Object proxy, ArooaSession session) {
			
			InvocationHandler handler = Proxy.getInvocationHandler(proxy);
			
			return ((FruitInvocationHandler) handler).impl;
		}
	}
	
   @Test
	public void testRoundTripWithProxy() throws ArooaParseException, ComponentPersistException {
		
		Root root = new Root();
		
		OurSession session = new OurSession();
		session.persister = new OurPersister();
		session.resolver = new OurResolver();
		
		StandardArooaParser parser = new StandardArooaParser(root, session);
		parser.parse(config);

		ComponentPool components = session.getComponentPool();
		
		root.fruit.setColour("red");
		
		components.save(root.fruit);

		root.fruit = null;
		
		OurSession session2 = new OurSession();
		session2.persister = session.persister;
		
		StandardArooaParser parser2 = new StandardArooaParser(root, session2);
		
		parser2.parse(config);
		
		assertEquals("red", root.fruit.getColour());

	}
}
