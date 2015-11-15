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

import static com.punyal.ahaio.AhUtils.getVPNaddress;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.List;
import java.util.logging.Level;
import se.bnearit.arrowhead.common.core.service.authorisation.AuthorisationControl;
import se.bnearit.arrowhead.common.core.service.authorisation.ws.rest.AuthorisationControlConsumerREST_WS;
import se.bnearit.arrowhead.common.core.service.discovery.exception.ServiceRegisterException;
import se.bnearit.arrowhead.common.core.service.orchestration.OrchestrationStore;
import se.bnearit.arrowhead.common.core.service.orchestration.ws.rest.OrchestrationStoreConsumerREST_WS;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@ltu.se>
 */
public class AhAIO {
    private static final Logger LOGGER = Logger.getLogger(AhAIO.class.getName());
    private final AhCore ahCore;
    private final AuthorisationControl authControl;
    private final OrchestrationStore orchestration;
    private final List<AhProducer> ahProducers;
    private final List<AhConsumer> ahConsumers;
    private final String trustStoreFile;
    private final String trustStorePassword;
    private final String keyStoreFile;
    private final String keyStorePassword;
    private final String tsigFile;

    public AhAIO(String trustStoreFile, String trustStorePassword, String keyStoreFile, String keyStorePassword, String tsigFile) {
        LOGGER.info("[Arrowhead All In One] Initialization");
        this.trustStoreFile = trustStoreFile;
        this.trustStorePassword = trustStorePassword;
        this.keyStoreFile = keyStoreFile;
        this.keyStorePassword = keyStorePassword;
        this.tsigFile = tsigFile;
        LOGGER.log(Level.INFO, "VPN address: {0}", getVPNaddress());
        ahCore = new AhCore(this.trustStoreFile, this.trustStorePassword, this.keyStoreFile, this.keyStorePassword, this.tsigFile);
        authControl = new AuthorisationControlConsumerREST_WS(ahCore.getAuthEndpoint(), ahCore.getClientFactory());
        orchestration = new OrchestrationStoreConsumerREST_WS(ahCore.getOrchEndpoint(), ahCore.getClientFactory());
        ahProducers = new ArrayList<>();
        ahConsumers = new ArrayList<>();
        // TODO: check if there are problems
        
        // Remove all the producers when exit
        Runtime.getRuntime().addShutdownHook(
                new Thread(){
                    @Override
                    public void run() {
                        removeAllProducers();
                    }
                }
        );
    }
    
    
    public void addProducer(String name, String serviceType, String path, String url, int port, boolean secure) {
        AhProducer ahProducer = new AhProducer(name, serviceType, path, url, port, secure, keyStoreFile, keyStorePassword, LOGGER, ahCore.getServiceDiscovery(), authControl);
        try {
            if (ahProducer.isRunning()) {
                //System.out.println("Running");
            } else {
                //System.out.println("Not Running");
            }
                
            if (ahProducer.isPublished()) {
                //System.out.println("Published");
                ahProducer.unpublish();
            } else {
                //System.out.println("Not Published");
                ahProducer.publish();
                ahProducers.add(ahProducer);
            }
            
            System.out.println("published");
        } catch (ServiceRegisterException ex) {
            //Logger.getLogger(AhAIO.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR: "+ex.getMessage());
            ahProducer.stop();
        }
    }
    
    private void removeProducer(AhProducer ahProducer) {
        //Logger.getLogger(AhAIO.class.getName()).log(Level.SEVERE, null, ex);
        
        if (ahProducer.isRunning()) {
            System.out.println("Running");
        } else {
            System.out.println("Not Running");
        }
        if (ahProducer.isPublished()) {
            System.out.println("Published");
            ahProducer.unpublish();
            System.out.println("UnPublished");
        } else {
            System.out.println("Not Published");
            //ahProducer.publish();
            //ahProducers.add(ahProducer);
        }
    }
    
    private synchronized void removeProducer(String name) {
        AhProducer toRemove = null;
        for (AhProducer producer : ahProducers) {
            if (producer.getName().equals(name)) {
                toRemove = producer;
            }
        }
        if (toRemove!=null) removeProducer(toRemove);
    }
    
    public void removeAllProducers() {
        System.out.println("Removing All the producers...");
        removeProducer("000Temp");
    }
    
    
}
