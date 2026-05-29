/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.*;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.utils.DateHelper;

public class SqlDateConvertletsTest extends Assert {


    @Test
    public void testDateToDate() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<java.util.Date, java.sql.Date> path = lookup.findConversion(
                java.util.Date.class, java.sql.Date.class);

        assertEquals("Date-Date", path.toString());

        java.sql.Date result = path.convert(DateHelper.parseDate("2010-07-02"), null);

        assertEquals("2010-07-02", DateHelper.formatDate(result));
    }

    @Test
    public void testStringToDate() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<String, java.sql.Date> path = lookup.findConversion(
                String.class, java.sql.Date.class);

        assertEquals("String-Date-Date", path.toString());

        java.sql.Date result = path.convert("2010-07-02", null);

        assertEquals(new java.sql.Date(DateHelper.parseDate("2010-07-02").getTime()), result);
    }

    @Test
    public void testDateToString() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<java.sql.Date, String> path = lookup.findConversion(
                java.sql.Date.class, String.class);

        assertEquals("Date-Date-String", path.toString());

        String result = path.convert(new java.sql.Date(
                DateHelper.parseDate("2010-07-02").getTime()), null);

        assertEquals("2010-07-02 00:00:00.000", result);
    }


    @Test
    public void testDateToArooaValue() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<java.sql.Date, ArooaValue> path = lookup.findConversion(
                java.sql.Date.class, ArooaValue.class);

        assertEquals("Date-Date-Object-ArooaValue", path.toString());

        ArooaValue result = path.convert(new java.sql.Date(0), null);

        assertEquals(new java.sql.Date(0), ((ArooaObject) result).getValue());
    }


}
