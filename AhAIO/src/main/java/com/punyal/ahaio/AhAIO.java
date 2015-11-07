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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.List;
import java.util.logging.Level;
import se.bnearit.arrowhead.common.core.service.authorisation.AuthorisationControl;
import se.bnearit.arrowhead.common.core.service.authorisation.ws.rest.AuthorisationControlConsumerREST_WS;
import se.bnearit.arrowhead.common.core.service.orchestration.OrchestrationStore;
import se.bnearit.arrowhead.common.core.service.orchestration.ws.rest.OrchestrationStoreConsumerREST_WS;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@ltu.se>
 */
public class AhAIO {
    private static final Logger LOGGER = Logger.getLogger(AhAIO.class.getName());
    private final AhCore ahCore;
    private AuthorisationControl authControl;
    private OrchestrationStore orchestration;
    
    
    private List<AhProducer> ahProducers;
    private List<AhConsumer> ahConsumers;

    public AhAIO(String trustStoreFile, String trustStorePassword, String keyStoreFile, String keyStorePassword) {
        LOGGER.info("[Arrowhead All In One] Initialization");
        LOGGER.log(Level.INFO, "VPN address: {0}", getVPNaddress());
        ahCore = new AhCore(trustStoreFile, trustStorePassword, keyStoreFile, keyStorePassword);
        authControl = new AuthorisationControlConsumerREST_WS(ahCore.getAuthEndpoint(), ahCore.getClientFactory());
        orchestration = new OrchestrationStoreConsumerREST_WS(ahCore.getOrchEndpoint(), ahCore.getClientFactory());
    }
    
    private String getVPNaddress() {
        String ip = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;
                if (iface.getDisplayName().substring(0,3).equals("VPN")) {
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while(addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        ip = addr.getHostAddress();
                        if (ip.substring(0, 3).equals("10."))
                            return ip;
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return ip;
    }
    
    
    
}
