package com.rrinat.fileparser;

import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class StringParseTest {

    private Charset characterSet = Charset.forName("US-ASCII");

    private byte[] array_0 = "\r\n".getBytes(characterSet);
    private byte[] array_1 = "some\r\nabc\r\nas3feSo3me\r\na4ttrree".getBytes(characterSet);
    private byte[] array_2 = "2\r\n3934590 some\r\nsdfsdf aaaabbbccc\r\n".getBytes(characterSet);
    private byte[] array_3 = "2\r\n3somedf3dff\r\n3sdsdgSo4me".getBytes(characterSet);
    private byte[] array_4 = "asf abc dfdf\r\ndfevd dfdf\r\n04fgf kfsdklgsdklgskl".getBytes(characterSet);
    private byte[] array_5 = "\naana w 34  erwerwer".getBytes(characterSet);
    private int lastIndex = 5;

    @Before
    public void initArrays() {
        for (int i = lastIndex; i < array_5.length; i++) array_5[i] = 0;
    }

    @Test
    public void parse() {
        List<String> result = parse("$");
        assertTrue(result.size() == 1);

        result = parse("*");
        assertTrue(result.size() == 13);

        result = parse("?$");
        assertTrue(result.size() == 1 && result.get(0).equals("2"));

        result = parse("*some*");
        assertTrue(result.size() == 3);

        result = parse("*some$");
        assertTrue(result.size() == 2);

        result = parse("?some*");
        assertTrue(result.size() == 1);

        result = parse("*So?me*");
        assertTrue(result.size() == 2);

        result = parse("*So?me$");
        assertTrue(result.size() == 1);

        result = parse("some$");
        assertTrue(result.size() == 1);
    }

    private List<String> parse(String pattern) {
        StringParser stringParser = new StringParser(pattern);
        stringParser.parse(array_0, array_0.length);
        stringParser.parse(array_1, array_1.length);
        stringParser.parse(array_2, array_2.length);
        stringParser.parse(array_3, array_3.length);
        stringParser.parse(array_4, array_4.length);
        stringParser.parse(array_5, lastIndex);

        stringParser.addLastLine();

        return stringParser.getLines();
    }
}
