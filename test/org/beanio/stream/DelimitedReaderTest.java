/*
 * Copyright 2010-2011 Kevin Seim
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beanio.stream;

import static org.junit.Assert.*;

import java.io.*;

import org.beanio.stream.delimited.*;
import org.junit.Test;

/**
 * JUnit test cases for <tt>DelimitedReader</tt> and <tt>DelimitedReaderFactory</tt>.
 * 
 * @author Kevin Seim
 */
public class DelimitedReaderTest {

    @Test
    public void testBasic() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        String[] expected = new String[] { "1", "2", "33", "444", "" };
        DelimitedReader in = createReader(factory, "1\t2\t33\t444\t\n");
        assertArrayEquals(expected, in.read());
        assertNull(in.read());
    }

    @Test
    public void testEscapeDisabled() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        factory.setEscape(null);
        DelimitedReader in = createReader(factory, "1\\\\\t2");
        assertArrayEquals(new String[] { "1\\\\", "2" }, in.read());
        assertNull(in.read());
    }

    @Test
    public void testEscapeEscape() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        factory.setEscape('\\');
        DelimitedReader in = createReader(factory, "1\\\\\t2");
        assertArrayEquals(new String[] { "1\\", "2" }, in.read());
        assertNull(in.read());
    }

    @Test
    public void testEscapeDelimiter() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        factory.setEscape('\\');
        DelimitedReader in = createReader(factory, "1\\\t\t2\\");
        assertArrayEquals(new String[] { "1\t", "2\\" }, in.read());
        assertNull(in.read());
    }

    @Test
    public void testEscapeOther() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        factory.setEscape('\\');
        DelimitedReader in = createReader(factory, "1\t2\\2");
        assertArrayEquals(new String[] { "1", "2\\2" }, in.read());
        assertNull(in.read());
    }

    @Test
    public void testCustomDelimiter() throws IOException {
        DelimitedReader in = new DelimitedReader(new StringReader("1,2,\t3"), ',');
        assertArrayEquals(new String[] { "1", "2", "\t3" }, in.read());
        assertNull(in.read());
    }

    @Test
    public void testLineContinuation() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        DelimitedReader in = createReader(factory, "1,2,\\\n3,4");
        assertArrayEquals(new String[] { "1", "2", "3", "4" }, in.read());
        assertEquals(in.getRecordLineNumber(), 1);
        assertNull(in.read());
    }

    @Test
    public void testLineContinuationCRLF() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        DelimitedReader in = createReader(factory, "1,2,\\\r\n3,4");
        assertArrayEquals(new String[] { "1", "2", "3", "4" }, in.read());
        assertEquals(in.getRecordLineNumber(), 1);
        assertNull(in.read());
    }

    @Test
    public void testLineContinuationIgnored() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        DelimitedReader in = createReader(factory, "1,2,\\3,4");
        assertArrayEquals(new String[] { "1", "2", "\\3", "4" }, in.read());
        assertEquals(in.getRecordLineNumber(), 1);
        assertNull(in.read());
    }

    @Test
    public void testLineContinuationAndEscape() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        factory.setEscape('\\');
        DelimitedReader in = createReader(factory, "1,2,\\3,4");
        assertArrayEquals(new String[] { "1", "2", "\\3", "4" }, in.read());
        assertEquals(in.getRecordLineNumber(), 1);
        assertNull(in.read());
    }

    @Test
    public void testLineNumber() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        DelimitedReader in = createReader(factory, "1,2,\\\n3,4\n5,6");
        assertArrayEquals(new String[] { "1", "2", "3", "4" }, in.read());
        assertEquals("1,2,\\\n3,4", in.getRecordText());
        assertEquals(1, in.getRecordLineNumber());
        assertArrayEquals(new String[] { "5", "6" }, in.read());
        assertEquals(3, in.getRecordLineNumber());
        assertNull(in.read());
    }

    @Test(expected = RecordIOException.class)
    public void testLineContinuationError() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        DelimitedReader in = createReader(factory, "1,2,\\");
        in.read();
    }

    @Test
    public void testCustomLineContinuation() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('#');
        DelimitedReader in = createReader(factory, "1,2,#\n3,4");
        assertArrayEquals(in.read(), new String[] { "1", "2", "3", "4" });
        assertNull(in.read());
    }

    @Test
    public void testLF() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        DelimitedReader in = createReader(factory, "1\t2\n3\t4");
        assertArrayEquals(in.read(), new String[] { "1", "2" });
        assertArrayEquals(in.read(), new String[] { "3", "4" });
        assertNull(in.read());
    }

    @Test
    public void testCRLF() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        DelimitedReader in = createReader(factory, "1\t2\r\n3\t4");
        assertArrayEquals(in.read(), new String[] { "1", "2" });
        assertArrayEquals(in.read(), new String[] { "3", "4" });
        assertNull(in.read());
    }

    @Test
    public void testCR() throws IOException {
        DelimitedReaderFactory factory = new DelimitedReaderFactory();
        DelimitedReader in = createReader(factory, "1\t2\r3\t4");
        assertArrayEquals(in.read(), new String[] { "1", "2" });
        assertArrayEquals(in.read(), new String[] { "3", "4" });
        assertNull(in.read());
    }

    @Test
    public void testClose() throws IOException {
        DelimitedReader in = new DelimitedReader(new StringReader(""));
        in.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelimiterCannotMatchContinuation() {
        new DelimitedReader(new StringReader(""), ',', '\\', ',');
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelimiterCannotMatchEscape() {
        new DelimitedReader(new StringReader(""), ',', ',', '\\');
    }

    @SuppressWarnings("unused")
    private void print(String[] sa) {
        for (String s : sa) {
            System.out.println(s);
        }
    }

    private DelimitedReader createReader(DelimitedReaderFactory factory, String input) {
        return (DelimitedReader) factory.createReader(createInput(input));
    }

    private Reader createInput(String s) {
        return new StringReader(s);
    }
}
