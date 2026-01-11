package org.oddjob.arooa.utils;

/**
 * The PrintWriter interface. It would be nice if this was in the JDK.
 */
public interface Printable extends Appendable {

    /**
     * Prints a boolean value.
     *
     * @param b The {@code boolean} to be printed
     *
     */
    void print(boolean b);

    /**
     * Prints a character.
     *
     * @param c The {@code char} to be printed
     */
    void print(char c);

    /**
     * Prints an integer.
     *
     * @param i The {@code int} to be printed
     */
    void print(int i);

    /**
     * Prints a long integer.
     *
     * @param l The {@code long} to be printed
     */
    void print(long l);

    /**
     * Prints a floating-point number.
     *
     * @param f The {@code float} to be printed
     */
    void print(float f);

    /**
     * Prints a double-precision floating-point number.
     *
     * @param d The {@code double} to be printed
     */
    void print(double d);

    /**
     * Prints a string.  If the argument is {@code null} then the string
     * {@code "null"} is printed.
     *
     * @param s The {@code String} to be printed
     */
    void print(String s);

    /**
     * Prints an object.  The string produced by the {@link
     * java.lang.String#valueOf(Object)} is written.
     *
     * @param obj The {@code Object} to be printed
     */
    void print(Object obj);

    /* Methods that do terminate lines */

    /**
     * Terminates the current line by writing the line separator string.  The
     * line separator is {@link System#lineSeparator()} and is not necessarily
     * a single newline character ({@code '\n'}).
     */
    void println();

    /**
     * Prints a boolean value and then terminates the line.  This method behaves
     * as though it invokes {@link #print(boolean)} and then
     * {@link #println()}.
     *
     * @param x the {@code boolean} value to be printed
     */
    void println(boolean x);

    /**
     * Prints a character and then terminates the line.  This method behaves as
     * though it invokes {@link #print(char)} and then {@link
     * #println()}.
     *
     * @param x the {@code char} value to be printed
     */
    void println(char x);

    /**
     * Prints an integer and then terminates the line.  This method behaves as
     * though it invokes {@link #print(int)} and then {@link
     * #println()}.
     *
     * @param x the {@code int} value to be printed
     */
    void println(int x);

    /**
     * Prints a long integer and then terminates the line.  This method behaves
     * as though it invokes {@link #print(long)} and then
     * {@link #println()}.
     *
     * @param x the {@code long} value to be printed
     */
    void println(long x);

    /**
     * Prints a floating-point number and then terminates the line.  This method
     * behaves as though it invokes {@link #print(float)} and then
     * {@link #println()}.
     *
     * @param x the {@code float} value to be printed
     */
    void println(float x);

    /**
     * Prints a double-precision floating-point number and then terminates the
     * line.  This method behaves as though it invokes {@link
     * #print(double)} and then {@link #println()}.
     *
     * @param x the {@code double} value to be printed
     */
    void println(double x);

    /**
     * Prints a String and then terminates the line.  This method behaves as
     * though it invokes {@link #print(String)} and then
     * {@link #println()}.
     *
     * @param x the {@code String} value to be printed
     */
    void println(String x);

    /**
     * Prints an Object and then terminates the line.  This method calls
     * at first String.valueOf(x) to get the printed object's string value,
     * then behaves as
     * though it invokes {@link #print(String)} and then
     * {@link #println()}.
     *
     * @param x The {@code Object} to be printed.
     */
    void println(Object x);

}
