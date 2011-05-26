package org.oddjob.arooa.convert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class DefaultConversionPath<F, T> implements ConversionPath<F, T> {

	private final List<ConversionStep<?, ?>> steps;

	private final Class<F> fromClass;
	private final Class<T> toClass;

	public static <X> ConversionPath<X, X> instance(Class<X> start) {

		return new DefaultConversionPath<X, X>(start, start, 
				new ArrayList<ConversionStep<?, ?>>());
	}
	
	private DefaultConversionPath(Class<F> from, Class<T> to,
			List<ConversionStep<?, ?>> steps) {
		this.steps = steps;
		
		this.fromClass = from;
		this.toClass = to;
	}
	
	public <X> ConversionPath<F, X> append(ConversionStep<T, X> following) {
		if (following == null) {
			throw new NullPointerException("ConversionStep can not be null.");
		}

		if (!following.getFromClass().equals(getToClass())) {
			throw new IllegalArgumentException("Can't append path with [" + 
					following.getFromClass() + "], expected [" + 
					getToClass() + "]");
		}
		
		List<ConversionStep<?, ?>> next = 
			new ArrayList<ConversionStep<?, ?>>(steps);
		
		next.add(following);
		
		return new DefaultConversionPath<F, X>(
				fromClass, following.getToClass(), next);
	}

	public <X> ConversionPath<X, T> prepend(ConversionStep<X, F> preceeding) {
		if (preceeding == null) {
			throw new NullPointerException("ConversionStep can not be null.");
		}
		
		if (!preceeding.getToClass().equals(getFromClass())) {
			throw new IllegalArgumentException("Can't preceed with [" + 
					preceeding.getToClass() + "]");
		}
		
		List<ConversionStep<?, ?>> next = 
			new ArrayList<ConversionStep<?, ?>>();
		
		next.add(preceeding);
		next.addAll(steps);
		
		return new DefaultConversionPath<X, T>(
				preceeding.getFromClass(), toClass, next);
	}
	
	public Class<F> getFromClass() {
		return fromClass;
	}
	
	public Class<T> getToClass() {
		return toClass;
	}
	
	public int length() {
		return steps.size();
	}
	
	public boolean contains(Class<?> from) {
		// check we're not going back on ourselves
		for (int i = 0; i < steps.size(); ++i) {
			if (((ConversionStep<?, ?>) steps.get(i)).getFromClass().equals(from)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public <X, Y> ConversionStep<X, Y> getStep(int step) {
		return (ConversionStep<X, Y>) steps.get(step);
	}

	/**
	 * Convert the given object using the ConversionPath.
	 * 
	 * @param from
	 * @return
	 * @throws ConvertletException
	 */
	@SuppressWarnings("unchecked")
	public T convert(F from, ArooaConverter converter) throws ConversionFailedException {

		OurConversionStack stack = new OurConversionStack();
		
		Object converted = from;
		for (int i = 0; i < steps.size(); ++i) {
			try {
				ConversionStep<Object, Object> nextStep = 
					(ConversionStep<Object, Object>) steps.get(i);
				
				Object before = converted;

				// apply this conversion
				converted = nextStep.convert(converted, converter);
				
				stack.addApplied(before, converted);
				if (converted == null) {
					return null;
				}
			} catch (Exception e) {
				stack.setConvertletException(e);
				throw new ConversionFailedException(stack, e);
			}
		}
		return (T) converted;
	}
	
	
	class OurConversionStack implements ConversionStack {
		
		private List<Object> applied = new ArrayList<Object>();
		private List<Object> before = new ArrayList<Object>();
		
		private Exception exception;
		
		void addApplied(Object from, Object converted) {
			before.add(from);
			applied.add(converted);
		}
		
		void setConvertletException(Exception e) {
			exception = e;
		}
		
		public ConversionPath<F, T> getConversionPath() {
			return DefaultConversionPath.this;
		}
		
		public Element getElement(final int index) {
			return new Element() {
				public Object getBefore() {
					if (index < before.size()) {
						return before.get(index);
					}
					return null;
				}
				public Object getConverted() {
					if (index < applied.size()) {
						return applied.get(index);
					}
					return null;
				}
				public Class<?> getFromClass() {
					return ((ConversionStep<?, ?>) steps.get(index)).getFromClass();
				}
				public Class<?> getToClass() {
					return ((ConversionStep<?, ?>) steps.get(index)).getToClass();
				}
				
			};
		}
	
		public int getFailedElementIndex() {
			return applied.size();
		}
		
		public int size() {
			return steps.size();
		}
	
		public void printStack(PrintStream out) {
			if (applied == null) {
				throw new IllegalStateException("No conversion attempted.");
			}
			
			for (int i = 0; i < applied.size(); ++i ) {
				Element appliedStep = (Element) getElement(i);
				out.println(appliedStep.getFromClass() + 
						" to " + appliedStep.getToClass() + ": [" +
						before.get(i) + "] to [" + applied.get(i) + "]");
			}
			
			if (exception == null) {
				return;
			}
			out.println(exception);
			
			for (int i = applied.size(); i < steps.size(); ++i) {
				if (i == applied.size()) {
					out.print("During: ");
				} else {
					out.print("Missed: ");
				}
				out.println("" + ((ConversionStep<?, ?>) steps.get(i)).getFromClass() + 
						" to " + ((ConversionStep<?, ?>) steps.get(i)).getToClass());
			}
		}
	
		public String getStackTrace() {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			printStack(new PrintStream(out));
			return new String(out.toByteArray());
		}

	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (ConversionStep<?, ?> step : steps) {
			if (builder.length() == 0) {
				builder.append(step.getFromClass().getSimpleName());
			}
			builder.append('-');
			builder.append(step.getToClass().getSimpleName());
		}
		return builder.toString();
	}
}
