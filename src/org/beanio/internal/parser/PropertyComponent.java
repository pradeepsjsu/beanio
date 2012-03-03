/*
 * Copyright 2012 Kevin Seim
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
package org.beanio.internal.parser;

/**
 * Base class for {@link Property} implementations that hold other properties (e.g. a bean object or collection).
 * 
 * <p>A <tt>PropertyComponent</tt> may only hold {@link Property} children.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class PropertyComponent extends Component implements Property {

    // the class type of the bean object
    private Class<?> type;
    // indicates whether the bean is always instantiated
    private boolean required;
    // whether any of this bean's children are used to identify an object for marshalling
    private boolean identifier;
    // the accessor for setting this bean on its parent, may be null
    private PropertyAccessor accessor;
    
    /**
     * Constructs a new <tt>PropertyComponent</tt>.
     */
    public PropertyComponent() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#isIdentifier()
     */
    public boolean isIdentifier() {
        return identifier;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#setIdentifier(boolean)
     */
    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#getAccessor()
     */
    public PropertyAccessor getAccessor() {
        return accessor;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#setAccessor(org.beanio.internal.parser.PropertyAccessor)
     */
    public void setAccessor(PropertyAccessor accessor) {
        this.accessor = accessor;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#getType()
     */
    public Class<?> getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#setType(java.lang.Class)
     */
    public void setType(Class<?> type) {
        this.type = type;
    }
    
    /**
     * Returns whether this property should always be instantiated when
     * {@link #createValue()} is invoked.
     * @return true to always instantiate this property, false otherwise
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets whether this property should always be instantiated when 
     * {@link #createValue()} is invoked.
     * @param required true to always instantiate this property, false otherwise
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    protected boolean isSupportedChild(Component child) {
        return child instanceof Property;
    }
    
    @Override
    protected void toParamString(StringBuilder s) {
        super.toParamString(s);
        s.append(", type=").append(type);
        s.append(", required=").append(required);
        s.append(", identifier=").append(identifier);
    }
}
