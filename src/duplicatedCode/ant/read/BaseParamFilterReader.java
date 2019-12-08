/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package duplicatedCode.ant.read;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Supplier;

import org.apache.tools.ant.filters.BaseFilterReader;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Parameterizable;

/**
 * Parameterized base class for core filter readers.
 *
 */
public abstract class BaseParamFilterReader
    extends BaseFilterReader
    implements Parameterizable {
    /** The passed in parameter array. */
    private Parameter[] parameters;

    /**
     * Constructor for "dummy" instances.
     *
     * @see BaseFilterReader#BaseFilterReader()
     */
    public BaseParamFilterReader() {
        super();
    }

    /**
     * Creates a new filtered reader.
     *
     * @param in A Reader object providing the underlying stream.
     *           Must not be <code>null</code>.
     */
    public BaseParamFilterReader(final Reader in) {
        super(in);
    }

    /**
     * Sets the parameters used by this filter, and sets
     * the filter to an uninitialized status.
     *
     * @param parameters The parameters to be used by this filter.
     *                   Should not be <code>null</code>.
     */
    public final void setParameters(final Parameter[] parameters) {
        this.parameters = parameters;
        setInitialized(false);
    }

    /**
     * Returns the parameters to be used by this filter.
     *
     * @return the parameters to be used by this filter
     */
    protected final Parameter[] getParameters() {
        return parameters;
    }

	/**
	 * Remaining line to be read from this filter, or <code>null</code> if the next call to <code>read()</code> should read the original stream to find the next matching line.
	 */
	protected String line = null;

	protected abstract void initialize();

	/**
	 * Find out whether we have been negated.
	 * @return  boolean negation flag.
	 */
	public boolean isNegated() {
		return negate;
	}

	protected boolean negate = false;

	/**
	 * Set the negation mode.  Default false (no negation).
	 * @param b  the boolean negation mode to set.
	 */
	public void setNegate(boolean b) {
		negate = b;
	}

	protected int readExtracted(Supplier<Boolean> matcher) throws IOException {
		if (!getInitialized()) {
			initialize();
			setInitialized(true);
		}
		int ch = -1;
		if (line != null) {
			ch = line.charAt(0);
			if (line.length() == 1) {
				line = null;
			} else {
				line = line.substring(1);
			}
		} else {
			for (line = readLine(); line != null; line = readLine()) {
				boolean matches = matcher.get();
				if (matches ^ isNegated()) {
					break;
				}
			}
			if (line != null) {
				return read();
			}
		}
		return ch;
	}
}
