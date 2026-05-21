package org.oddjob.arooa.convert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class DefaultConversionPath<F, T> implements ConversionPath<F, T> {

	private final List<ConversionStep<?, ?>> steps;

	private final TypeArooa<F> fromType;
	private final TypeArooa<T> toType;

    public static <X> ConversionPath<X, X> instance(Class<X> start) {

        return instance(TypeArooa.of(start));
    }

	public static <X> ConversionPath<X, X> instance(TypeArooa<X> start) {

		return new DefaultConversionPath<>(start, start,
                new ArrayList<>());
	}
	
	private DefaultConversionPath(TypeArooa<F> from, TypeArooa<T> to,
			List<ConversionStep<?, ?>> steps) {
		this.steps = steps;
		
		this.fromType = from;
		this.toType = to;
	}
	
	public <X> ConversionPath<F, X> append(ConversionStep<T, X> following) {
		if (following == null) {
			throw new NullPointerException("ConversionStep can not be null.");
		}

		if (!following.getFromType().equals(getToType())) {
			throw new IllegalArgumentException("Can't append path with [" + 
					following.getFromType() + "], expected [" +
					getToType() + "]");
		}
		
		List<ConversionStep<?, ?>> next =
                new ArrayList<>(steps);
		
		next.add(following);
		
		return new DefaultConversionPath<>(
                fromType, following.getToType(), next);
	}

	public <X> ConversionPath<X, T> prepend(ConversionStep<X, F> preceding) {
		if (preceding == null) {
			throw new NullPointerException("ConversionStep can not be null.");
		}
		
		if (!preceding.getToClass().equals(getFromClass())) {
			throw new IllegalArgumentException("Can't preceed with [" + 
					preceding.getToClass() + "]");
		}
		
		List<ConversionStep<?, ?>> next =
                new ArrayList<>();
		
		next.add(preceding);
		next.addAll(steps);
		
		return new DefaultConversionPath<>(
                preceding.getFromType(), toType, next);
	}
	
	public TypeArooa<F> getFromType() {
		return fromType;
	}
	
	public TypeArooa<T> getToType() {
		return toType;
	}
	
	public int length() {
		return steps.size();
	}
	
	public boolean contains(TypeArooa<?> type) {
		// check we're not going back on ourselves
        for (ConversionStep<?, ?> step : steps) {
            if (step.getFromType().equals(type)) {
                return true;
            }
        }
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public <X, Y> ConversionStep<X, Y> getStep(int step) {
		return (ConversionStep<X, Y>) steps.get(step);
	}

	@SuppressWarnings("unchecked")
	public T convert(F from, ArooaConverter converter) throws ConversionFailedException {

		OurConversionStack stack = new OurConversionStack();
		
		Object converted = from;
        for (ConversionStep<?, ?> step : steps) {
            try {
                ConversionStep<Object, Object> nextStep =
                        (ConversionStep<Object, Object>) step;

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
		
		private final List<Object> applied = new ArrayList<>();
		private final List<Object> before = new ArrayList<>();
		
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
				public TypeArooa<?> getFromType() {
					return steps.get(index).getFromType();
				}
				public TypeArooa<?> getToType() {
					return steps.get(index).getToType();
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
			for (int i = 0; i < applied.size(); ++i ) {
				Element appliedStep = getElement(i);
				out.println(appliedStep.getFromType() +
						" to " + appliedStep.getToType() + ": [" +
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
				out.println(steps.get(i).getFromType() +
						" to " + steps.get(i).getToType());
			}
		}
	
		public String getStackTrace() {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			printStack(new PrintStream(out));
			return out.toString();
		}

	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (ConversionStep<?, ?> step : steps) {
			if (builder.isEmpty()) {
				builder.append(step.getFromType().getRawType().getSimpleName());
			}
			builder.append('-');
			builder.append(step.getToType().getRawType().getSimpleName());
		}
		return builder.toString();
	}
}
