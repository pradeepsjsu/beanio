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

import java.io.Writer;

import org.beanio.stream.*;

/**
 * This record writer factory is used to create and configure a <tt>CsvWriter</tt>.
 * 
 * @author Kevin Seim
 * @since 1.0
 * @see CsvWriter
 */
public class CsvWriterFactory implements RecordWriterFactory {

    private char delimiter = ',';
    private char quote = '"';
    private char escapeChararcter = '"';
    private String lineSeparator = null;
    private boolean alwaysQuote = false;
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriterFactory#createWriter(java.io.Writer)
     */
	public RecordWriter createWriter(Writer out) {
		return new CsvWriter(out, delimiter, quote, alwaysQuote, escapeChararcter,
			lineSeparator);
	}

    /**
     * Sets the field delimiter.  By default, the delimiter is a comma.
     * @param c the character used to delimit fields
     */
    public void setDelimiter(char c) {
        this.delimiter = c;
    }
    
    /**
     * Returns the character to use for a quotation mark.  Defaults to '"'.
     * @return the quotation mark character
     */
    public char getQuote() {
		return quote;
	}

    /**
     * Sets the character to use for a quotation mark.
     * @param quote the new quotation mark character
     */
	public void setQuote(char quote) {
		this.quote = quote;
	}
    
    /**
     * Returns the field delimiter.  By default, the delimiter is a comma.
     * @return the character used to delimit fields
     */
    public char getDelimiter() {
        return delimiter;
    }
    
	/**
     * Sets the escape character.  Quotation marks are escaped within quoted values
     * using the escape character.
     * @param c the new escape character
     */
    public void setEscapeCharacter(char c) {
        this.escapeChararcter = c;
    }
    
    /**
     * Returns the escape character.  Quotation marks are escaped within quoted values
     * using the escape character.
     * @return the escape character
     */
    public char getEscapeCharacter() {
        return escapeChararcter;
    }
    
    /**
     * Returns <tt>true</tt> if fields should always be quoted.  Defaults to
     * <tt>false</tt> which will only quote fields containing a quotation mark,
     * delimiter, line feeds or carriage return.
     * @return <tt>true</tt> if all fields will be quoted
     */
    public boolean isAlwaysQuote() {
		return alwaysQuote;
	}

    /**
     * Set to <tt>true</tt> to quote every field.  If <tt>false</tt>, a field
     * will only be quoted if it contains a quotation mark, delimiter, line feed
     * or carriage return.
     * @param alwaysQuote set to <tt>true</tt> to quote every field regardless
     *   of content
     */
	public void setAlwaysQuote(boolean alwaysQuote) {
		this.alwaysQuote = alwaysQuote;
	}

	/**
     * Returns the text used to terminate a record.  By default, the
     * line separator is set using the 'line.separator' system property.
     * @return the line separation text
     */
	public String getLineSeparator() {
		return lineSeparator;
	}

	/**
	 * Sets the text used to terminate a record.  If set to <tt>null</tt>, the default
     * line separator is used based on the 'line.separator' system property.
	 * @param lineSeparator the line separation text
	 */
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
}
