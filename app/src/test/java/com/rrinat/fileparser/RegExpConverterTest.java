package com.rrinat.fileparser;


import org.junit.Test;


import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RegExpConverterTest {

    @Test
    public void normalize() {
        RegExpConverter converter = new RegExpConverter();

        assertEquals(converter.normalize(""), "$");
        assertEquals(converter.normalize("*"), "*");
        assertEquals(converter.normalize("**"), "*");
        assertEquals(converter.normalize("?"), "?$");
        assertEquals(converter.normalize("*Some*"), "*Some*");
        assertEquals(converter.normalize("*Some"), "*Some$");
        assertEquals(converter.normalize("?Some**"), "?Some*");
        assertEquals(converter.normalize("***So?me"), "*So?me$");
    }
}