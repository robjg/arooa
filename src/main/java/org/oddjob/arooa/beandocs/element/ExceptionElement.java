package org.oddjob.arooa.beandocs.element;

/**
 * Wrap an Exception. Exceptions in Bean Doc are normally caused by failing to
 * load an include.
 */
public class ExceptionElement implements BeanDocElement {

    private String message;

    public static ExceptionElement of(Exception e) {
        ExceptionElement exceptionElement = new ExceptionElement();
        exceptionElement.setMessage(e.toString());
        return exceptionElement;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public <C, R> R accept(ElementVisitor<C, R> visitor, C context) {
        return visitor.visitException(this, context);
    }
}
