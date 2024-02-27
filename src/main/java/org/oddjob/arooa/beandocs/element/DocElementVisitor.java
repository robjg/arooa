package org.oddjob.arooa.beandocs.element;

/**
 * Visits an {@link BeanDocElement}.
 *
 * @param <C> The type of the Context that will be passed through.
 * @param <R> The type of the Result that this visitor will return.
 */
public interface DocElementVisitor<C, R> {

    R visitInternalLink(LinkElement element, C context);

    R visitPreformattedBlock(PreformattedBlock element, C context);

    R visitJavaCodeBlock(JavaCodeBlock element, C context);

    R visitXmlBlock(XmlBlock element, C context);

    R visitException(ExceptionElement element, C context);

    R visitLiteral(LiteralElement element, C context);

    R visitCode(CodeElement element, C context);

    R visitStandard(StandardElement element, C context);

    R visitStartHtmlElement(StartHtmlElement element, C context);

    R visitEndHtmlElement(EndHtmlElement element, C context);
}
