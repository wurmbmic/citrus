/*
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.model.config.core;

/**
 * Builder for creating schema repository instances.
 *
 * @author Martin.Maher@consol.de
 * @since 2013.04.19
 */
public class XsdSchemaBuilder {

    /**
     * Model object
     */
    private XsdSchema model = new XsdSchema();

    /**
     * Default constructor
     */
    public XsdSchemaBuilder() {
    }

    /**
     * Set the id.
     *
     * @param id
     * @return
     */
    public XsdSchemaBuilder withId(String id) {
        model.setId(id);
        return this;
    }

    /**
     * Set the location.
     *
     * @param location the location (classpath of file)
     * @return
     */
    public XsdSchemaBuilder setLocation(String location) {
        model.setLocation(location);
        return this;
    }

    /**
     * Builds the model.
     *
     * @return
     */
    public XsdSchema build() {
        return model;
    }
}
