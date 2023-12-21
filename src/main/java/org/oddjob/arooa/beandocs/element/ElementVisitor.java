package org.oddjob.arooa.beandocs.element;

/**
 * Visits an {@link BeanDocElement}.
 *
 * @param <C> The type of the Context that will be passed through.
 * @param <R> The type of the Result that this visitor will return.
 */
public interface ElementVisitor<C, R> {

    R visitInternalLink(InternalLink element, C context);

    R visitPreformattedBlock(PreformattedBlock element, C context);

    R visitJavaCodeBlock(JavaCodeBlock element, C context);

    R visitXmlBlock(XmlBlock element, C context);

    R visitException(ExceptionElement element, C context);

    R visitStandard(StandardElement element, C context);
}
