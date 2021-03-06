/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.dsl.endpoint;

import com.consol.citrus.http.client.HttpClientBuilder;
import com.consol.citrus.http.server.HttpServerBuilder;
import com.consol.citrus.jms.endpoint.JmsEndpointBuilder;
import com.consol.citrus.jms.endpoint.JmsSyncEndpointBuilder;
import com.consol.citrus.jmx.client.JmxClientBuilder;
import com.consol.citrus.jmx.server.JmxServerBuilder;
import com.consol.citrus.rmi.client.RmiClientBuilder;
import com.consol.citrus.rmi.server.RmiServerBuilder;
import com.consol.citrus.ws.client.WebServiceClientBuilder;
import com.consol.citrus.ws.server.WebServiceServerBuilder;

/**
 * @author Christoph Deppisch
 * @since 2.5
 */
public abstract class CitrusEndpoints {

    /**
     * Prevent public instantiation.
     */
    protected CitrusEndpoints() {
        super();
    }

    /**
     * Creates new JmsEndpoint sync or async builder.
     * @return
     */
    public static AsyncSyncEndpointBuilder<JmsEndpointBuilder, JmsSyncEndpointBuilder> jms() {
        return new AsyncSyncEndpointBuilder<>(new JmsEndpointBuilder(), new JmsSyncEndpointBuilder());
    }

    /**
     * Creates new HttpClient or HttpServer builder.
     * @return
     */
    public static ClientServerEndpointBuilder<HttpClientBuilder, HttpServerBuilder> http() {
        return new ClientServerEndpointBuilder<>(new HttpClientBuilder(), new HttpServerBuilder());
    }

    /**
     * Creates new WebServiceClient or WebServiceServer builder.
     * @return
     */
    public static ClientServerEndpointBuilder<WebServiceClientBuilder, WebServiceServerBuilder> soap() {
        return new ClientServerEndpointBuilder<>(new WebServiceClientBuilder(), new WebServiceServerBuilder());
    }

    /**
     * Creates new JmxClient or JmxServer builder.
     * @return
     */
    public static ClientServerEndpointBuilder<JmxClientBuilder, JmxServerBuilder> jmx() {
        return new ClientServerEndpointBuilder<>(new JmxClientBuilder(), new JmxServerBuilder());
    }

    /**
     * Creates new RmiClient or RmiServer builder.
     * @return
     */
    public static ClientServerEndpointBuilder<RmiClientBuilder, RmiServerBuilder> rmi() {
        return new ClientServerEndpointBuilder<>(new RmiClientBuilder(), new RmiServerBuilder());
    }
}
