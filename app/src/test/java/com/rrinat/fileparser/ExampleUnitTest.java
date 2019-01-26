package com.rrinat.fileparser;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void compareRexExp() {
        assertTrue(Pattern.compile(".*Abc").matcher("фывфыв 1211 Abc").matches());
        assertFalse(Pattern.compile(".*Abc").matcher("aaabbbbbccc").matches());
        assertFalse(Pattern.compile(".*Abc").matcher("sdsd Abc sdsd").matches());
        assertTrue(Pattern.compile(".*Abc.*").matcher("Abc").matches());
        assertTrue(Pattern.compile(".*Abc.*").matcher("222 Abc ccc3333").matches());
        assertTrue(Pattern.compile("^abc.*").matcher("abc 22222").matches());
        assertTrue(Pattern.compile("^abc.*").matcher("abc").matches());
        assertFalse(Pattern.compile("^abc.*").matcher("333 abc").matches());
        assertTrue(Pattern.compile("^abc.{1}").matcher("abc3").matches());
        assertTrue(Pattern.compile("^abc.{1}aa").matcher("abc3aa").matches());
        assertFalse(Pattern.compile("^abc.{1}").matcher("abc").matches());
        assertTrue(Pattern.compile("^$").matcher("").matches());
    }

    @Test
    public void converter() {
        RegExpConverter converter = new RegExpConverter();

        assertEquals(converter.convertPattern("******Some*****").pattern(), ".*Some.*");
        assertEquals(converter.convertPattern("").pattern(), "^$");
        assertEquals(converter.convertPattern("abc").pattern(), "^abc$");
        assertEquals(converter.convertPattern("*abc").pattern(), ".*abc$");
        assertEquals(converter.convertPattern("abc***").pattern(), "^abc.*");

        assertEquals(converter.convertPattern("abcb?asds").pattern(), "^abcb.{1}asds$");
        assertEquals(converter.convertPattern("abc**ddfd?").pattern(), "^abc.*ddfd.{1}$");
        assertEquals(converter.convertPattern("a?abc").pattern(), "^a.{1}abc$");
    }
}