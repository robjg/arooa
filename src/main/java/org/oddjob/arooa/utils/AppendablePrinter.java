package org.oddjob.arooa.utils;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Provides an Appendable with the same methods a PrintWriter has.
 *
 */
public class AppendablePrinter implements Printable {

    private final Appendable out;

    public AppendablePrinter(Appendable out) {
        this.out = out;
    }

    private void write(CharSequence s) {
        try {
            out.append(s);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void write(char c) {
        try {
            out.append(c);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void newLine() {
        write(System.lineSeparator());
    }

    /**
     * Prints a boolean value.
     *
     * @param b The {@code boolean} to be printed
     *
     */
    @Override
    public void print(boolean b) {
        write(String.valueOf(b));
    }

    /**
     * Prints a character.
     *
     * @param c The {@code char} to be printed
     */
    @Override
    public void print(char c) {
        write(c);
    }

    /**
     * Prints an integer.
     *
     * @param i The {@code int} to be printed
     */
    @Override
    public void print(int i) {
        write(String.valueOf(i));
    }

    /**
     * Prints a long integer.
     *
     * @param l The {@code long} to be printed
     */
    @Override
    public void print(long l) {
        write(String.valueOf(l));
    }

    /**
     * Prints a floating-point number.
     *
     * @param f The {@code float} to be printed
     */
    @Override
    public void print(float f) {
        write(String.valueOf(f));
    }

    /**
     * Prints a double-precision floating-point number.
     *
     * @param d The {@code double} to be printed
     */
    @Override
    public void print(double d) {
        write(String.valueOf(d));
    }

    /**
     * Prints a string.  If the argument is {@code null} then the string
     * {@code "null"} is printed.
     *
     * @param s The {@code String} to be printed
     */
    @Override
    public void print(String s) {
        write(String.valueOf(s));
    }

    /**
     * Prints an object.  The string produced by the {@link
     * java.lang.String#valueOf(Object)} is written.
     *
     * @param obj The {@code Object} to be printed
     */
    @Override
    public void print(Object obj) {
        write(String.valueOf(obj));
    }

    /* Methods that do terminate lines */

    /**
     * Terminates the current line by writing the line separator string.  The
     * line separator is {@link System#lineSeparator()} and is not necessarily
     * a single newline character ({@code '\n'}).
     */
    @Override
    public void println() {
        newLine();
    }

    /**
     * Prints a boolean value and then terminates the line.  This method behaves
     * as though it invokes {@link #print(boolean)} and then
     * {@link #println()}.
     *
     * @param x the {@code boolean} value to be printed
     */
    @Override
    public void println(boolean x) {
        print(x);
        println();
    }

    /**
     * Prints a character and then terminates the line.  This method behaves as
     * though it invokes {@link #print(char)} and then {@link
     * #println()}.
     *
     * @param x the {@code char} value to be printed
     */
    @Override
    public void println(char x) {
        print(x);
        println();
    }

    /**
     * Prints an integer and then terminates the line.  This method behaves as
     * though it invokes {@link #print(int)} and then {@link
     * #println()}.
     *
     * @param x the {@code int} value to be printed
     */
    @Override
    public void println(int x) {
        print(x);
        println();
    }

    /**
     * Prints a long integer and then terminates the line.  This method behaves
     * as though it invokes {@link #print(long)} and then
     * {@link #println()}.
     *
     * @param x the {@code long} value to be printed
     */
    @Override
    public void println(long x) {
        print(x);
        println();
    }

    /**
     * Prints a floating-point number and then terminates the line.  This method
     * behaves as though it invokes {@link #print(float)} and then
     * {@link #println()}.
     *
     * @param x the {@code float} value to be printed
     */
    @Override
    public void println(float x) {
        print(x);
        println();
    }

    /**
     * Prints a double-precision floating-point number and then terminates the
     * line.  This method behaves as though it invokes {@link
     * #print(double)} and then {@link #println()}.
     *
     * @param x the {@code double} value to be printed
     */
    @Override
    public void println(double x) {
        print(x);
        println();
    }


    /**
     * Prints a String and then terminates the line.  This method behaves as
     * though it invokes {@link #print(String)} and then
     * {@link #println()}.
     *
     * @param x the {@code String} value to be printed
     */
    @Override
    public void println(String x) {
        print(x);
        println();
    }

    /**
     * Prints an Object and then terminates the line.  This method calls
     * at first String.valueOf(x) to get the printed object's string value,
     * then behaves as
     * though it invokes {@link #print(String)} and then
     * {@link #println()}.
     *
     * @param x The {@code Object} to be printed.
     */
    @Override
    public void println(Object x) {
        String s = String.valueOf(x);
        print(s);
        println();
    }

    /**
     * Appends the specified character sequence to this Appendable.
     *
     * @param csq The character sequence to append.  If {@code csq} is
     *            {@code null}, then the four characters {@code "null"} are
     *            appended to this writer.
     * @return This writer
     *
     */
    @Override
    public AppendablePrinter append(CharSequence csq) {
        write(String.valueOf(csq));
        return this;
    }

    /**
     * Appends a subsequence of the specified character sequence to this Appendable.
     *
     * @param csq   The character sequence from which a subsequence will be
     *              appended.  If {@code csq} is {@code null}, then characters
     *              will be appended as if {@code csq} contained the four
     *              characters {@code "null"}.
     * @param start The index of the first character in the subsequence
     * @param end   The index of the character following the last character in the
     *              subsequence
     * @return This writer
     * @throws IndexOutOfBoundsException If {@code start} or {@code end} are negative, {@code start}
     *                                   is greater than {@code end}, or {@code end} is greater than
     *                                   {@code csq.length()}
     */
    @Override
    public AppendablePrinter append(CharSequence csq, int start, int end) {
        if (csq == null) csq = "null";
        return append(csq.subSequence(start, end));
    }

    /**
     * Appends the specified character to this Appendable.
     *
     * @param c The 16-bit character to append
     * @return This
     */
    @Override
    public AppendablePrinter append(char c) {
        write(c);
        return this;
    }


}
