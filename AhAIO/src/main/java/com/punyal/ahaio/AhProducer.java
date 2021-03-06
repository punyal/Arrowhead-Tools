/*
 * The MIT License
 *
 * Copyright Error: on line 6, column 29 in Templates/Licenses/license-mit.txt
 The string doesn't match the expected date/time format. The string to parse was: "06-nov-2015". The expected format was: "MMM d, yyyy". Pablo Puñal Pereira <pablo.punal@ltu.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.punyal.ahaio;

import java.util.logging.Logger;
import se.bnearit.arrowhead.common.core.service.authorisation.AuthorisationControl;
import se.bnearit.arrowhead.common.core.service.discovery.ServiceDiscovery;
import se.bnearit.arrowhead.common.core.service.discovery.endpoint.HttpEndpoint;
import se.bnearit.arrowhead.common.service.ServiceProducer;
import se.bnearit.arrowhead.common.service.ws.rest.BaseProviderREST_WS;
import se.bnearit.resource.ResourceAllocator;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@ltu.se>
 */
class AhProducer extends BaseProviderREST_WS implements ServiceProducer {
    private final AuthorisationControl authControl;
    private final int resourceId;
    private final String name;
    private final String serviceType;
    private final String path;
    private final String url;
    private final int port;
    
    public AhProducer(String name, String serviceType, String path, String url, int port, boolean secure, String keyStoreFile, String keyStorePassword, Logger LOGGER, ServiceDiscovery serviceDiscovery, AuthorisationControl authControl) {
        super(name, serviceType, secure, keyStoreFile, keyStorePassword, LOGGER, serviceDiscovery);
        this.name = name;
        this.serviceType = serviceType;
        this.path = path;
        this.port = port;
        this.authControl = authControl;
        this.url = url;
        resourceId = ResourceAllocator.getInstance().allocateResourceId();
        this.endpoint = new HttpEndpoint(url, port, path);
    }
    
    public String getPath(){
        return path;
    }
    
    public String getURL(){
        return url;
    }
    
    public int getPort(){
        return port;
    }
    
}
