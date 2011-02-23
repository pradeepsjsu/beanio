/*
 * Copyright 2010 Kevin Seim
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
package org.beanio.stream.csv;

import java.io.*;

import org.beanio.stream.RecordWriter;

/**
 * A <tt>CsvWriter</tt> is used to format and write records, of <tt>String</tt> arrays,
 * to a CSV output stream.  Using default settings, the CSV format supported is defined 
 * by specification RFC 4180.
 * <p>
 * The writer also supports the following customizations:
 * <ul>
 * <li>The default field delimiter, ',', may be overridden.</li>
 * <li>The default quotation mark, '"', may be overridden.</li>
 * <li>The default escape character, '"', may be overridden.</li>
 * <li>The writer can be configured to qutoe every field.  Otherwise a
 *   field is only quoted if it contains a quotation mark, delimiter, 
 *   line feed or carriage return.</li>
 * </ul>
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class CsvWriter implements RecordWriter {

    private char delim = ',';
    private char quote = '"';
    private char endQuote = '"';
    private char escapeChar = '"';
    private String lineSeparator;
    private boolean alwaysQuote = false;
    
    private transient Writer out;
    private transient int lineNumber;
    
    /**
     * Constructs a new <tt>CsvWriter</tt> using default settings 
     * according the RFC 4180 specification.
     * @param out the output stream to write to
     */
    public CsvWriter(Writer out) {
        this(out, false, '"');
    }
    
   /**
    * Constructs a new <tt>CsvWriter</tt>.
    * @param out the output stream to write to
    * @param alwaysQuote set to <tt>true</tt> if all fields are always quoted, otherwise
    *   a field is only quoted if it contains a quotation mark, delimiter, new line
    *   or carriage return
    * @param escapeCharacter the character used to escape quotation marks and itself
    *   in the output
    */
   public CsvWriter(Writer out, boolean alwaysQuote, char escapeCharacter) {
       this(out, ',', '"', alwaysQuote, escapeCharacter, null);
   }
    
    /**
     * Constructs a new <tt>CsvWriter</tt>.
     * @param out the output stream to write to
     * @param delimiter the field delimiter
     * @param quote the quotation mark
     * @param alwaysQuote set to <tt>true</tt> if all fields are always quoted, otherwise
     *   a field is only quoted if it contains a quotation mark, delimiter, line feed
     *   or carriage return
     * @param escapeCharacter the character used to escape quotation marks and itself
     *   in the output
     * @param lineSeparator the line termination character
     */
    public CsvWriter(Writer out, char delimiter, char quote, boolean alwaysQuote,
    	char escapeCharacter, String lineSeparator) {
    	this.out = out;
    	this.delim = delimiter;
    	this.quote = quote;
    	this.endQuote = quote;
    	this.alwaysQuote = alwaysQuote;
    	this.escapeChar = escapeCharacter;
    	if (lineSeparator == null) {
    		lineSeparator = System.getProperty("line.separator");
    	}
    	this.lineSeparator = lineSeparator;
    }
    
    /**
     * Returns the last line number written to the output stream.
     * @return the last line number
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriter#write(java.lang.Object)
     */
    public void write(Object value) throws IOException {
    	write((String[])value);
    }
    
    /**
     * Formats and writes a record to this output stream.
     * @param record the record to write
     * @throws IOException if an I/O error occurs
     */
    public void write(String [] record) throws IOException {
        ++lineNumber;
        
        int pos = 0;
        for (String field : record) {
            if (pos++ > 0)
                out.write(delim);
            
            boolean skipLF = false;
            char [] cs = field.toCharArray();
            
            boolean quoted = alwaysQuote || mustQuote(cs);
            if (quoted) {
                out.write(quote);
            }
            
            for (char c : cs) {
                if (c == endQuote || c == escapeChar) {
                    out.write(escapeChar);
                }
                else if (c == '\r') {
                    skipLF = true;
                    ++lineNumber;
                }
                else if (c == '\n') {
                    if (skipLF) {
                        skipLF = false;
                    }
                    else {
                        ++lineNumber;
                    }
                }
                else {
                    skipLF = false;
                }
                
                out.write(c);
            }
            
            if (quoted) {
                out.write(endQuote);
            }
        }
        out.write(lineSeparator);
    }
    
    /**
     * Returns <tt>true</tt> if the given field must be quoted.
     * @param cs the field to test
     * @return <tt>true</tt> if the given field must be quoted
     */
    private boolean mustQuote(char [] cs) {
        for (char c : cs) {
            if (c == delim)
                return true;
            if (c == quote)
                return true;
            if (c == '\n')
                return true;
            if (c == '\r')
                return true;
        }
        return false;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriter#flush()
     */
    public void flush() throws IOException {
        out.flush();
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriter#close()
     */
    public void close() throws IOException {
        out.close();
    }
}
