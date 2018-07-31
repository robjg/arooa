package org.oddjob.arooa.convert;

/**
 * A FinalConvertlet is a {@link Convertlet} that can only 
 * be used in a ConversionPath if it converts to 
 * actual thing required. It can not be used as a
 * stepping stone.
 * 
 * @author rob
 *
 */
public interface FinalConvertlet<F, T> extends Convertlet<F, T> {

}
