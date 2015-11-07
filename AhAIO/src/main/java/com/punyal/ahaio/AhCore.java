/*
 * The MIT License
 *
 * Copyright 2015 Pablo Puñal Pereira <pablo.punal@ltu.se>.
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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.bnearit.arrowhead.common.core.service.authorisation.AuthorisationControl;
import se.bnearit.arrowhead.common.core.service.authorisation.ws.rest.AuthorisationControlConsumerREST_WS;
import se.bnearit.arrowhead.common.core.service.authorisation.ws.rest.AuthorisationServiceTypes;
import se.bnearit.arrowhead.common.core.service.discovery.dnssd.ServiceDiscoveryDnsSD;
import se.bnearit.arrowhead.common.core.service.discovery.endpoint.HttpEndpoint;
import se.bnearit.arrowhead.common.core.service.orchestration.OrchestrationStore;
import se.bnearit.arrowhead.common.core.service.orchestration.ws.rest.OrchestrationServiceTypes;
import se.bnearit.arrowhead.common.core.service.orchestration.ws.rest.OrchestrationStoreConsumerREST_WS;
import se.bnearit.arrowhead.common.service.ServiceIdentity;
import se.bnearit.arrowhead.common.service.ServiceInformation;
import se.bnearit.arrowhead.common.service.ws.rest.ClientFactoryREST_WS;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@ltu.se>
 */
class AhCore {
    private static final Logger LOGGER = Logger.getLogger(AhCore.class.getName());
    private final ServiceDiscoveryDnsSD serviceDiscovery;
    private final ClientFactoryREST_WS clientFactoryREST_WS;
    private HttpEndpoint authEndpoint;
    private HttpEndpoint orchEndpoint;
    
    
    public AhCore(String trustStoreFile, String trustStorePassword, String keyStoreFile, String keyStorePassword) {
        serviceDiscovery = new ServiceDiscoveryDnsSD();
        clientFactoryREST_WS = new ClientFactoryREST_WS(trustStoreFile, trustStorePassword, keyStoreFile, keyStorePassword);
        authEndpoint = findAndCreateEndpoint(AuthorisationServiceTypes.REST_WS_AUTHORISATION_CTRL_SECURE);
        if (authEndpoint == null) {
            authEndpoint = HttpEndpoint.createFromString("https://10.200.0.10:8181/authorisation-control");
            LOGGER.log(Level.WARNING, "Default Authentication EndPoint: {0}", authEndpoint.toString());
        }
        orchEndpoint = findAndCreateEndpoint(OrchestrationServiceTypes.REST_WS_ORCHESTRATION_STORE_SECURE);
        if (orchEndpoint == null) {
            orchEndpoint = HttpEndpoint.createFromString("https://10.200.0.10:8181/orchestration-store");
            LOGGER.log(Level.WARNING, "Default Orchestration EndPoint: {0}", orchEndpoint.toString());
        }
    }
    
    private HttpEndpoint findAndCreateEndpoint(String serviceType) {
        HttpEndpoint endpoint = null;
        if (serviceDiscovery != null) {
            List<ServiceIdentity> authServices = serviceDiscovery.getServicesByType(serviceType);
            if (authServices.size() > 0) {
                ServiceIdentity authServiceId = authServices.get(0);
                ServiceInformation authServiceInfo = serviceDiscovery.getServiceInformation(authServiceId, HttpEndpoint.ENDPOINT_TYPE);
                if (authServiceInfo != null) {
                    endpoint = (HttpEndpoint) authServiceInfo.getEndpoint();
                    LOGGER.log(Level.INFO, "Found service {0}", authServiceId);
                } else
                    LOGGER.log(Level.WARNING, "Could not get service information about {0}", authServiceId);
            } else
                LOGGER.log(Level.INFO, "Could not find any services of type{0}", serviceType);
        }	
        return endpoint;
    }
    
    public HttpEndpoint getAuthEndpoint() {
        return authEndpoint;
    }
    
    public HttpEndpoint getOrchEndpoint() {
        return orchEndpoint;
    }
    
    public ClientFactoryREST_WS getClientFactory() {
        return clientFactoryREST_WS;
    }
            
}
