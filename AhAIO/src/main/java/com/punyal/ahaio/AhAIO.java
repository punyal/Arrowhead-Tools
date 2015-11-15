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
import se.bnearit.arrowhead.common.service.ServiceIdentity;
import se.bnearit.arrowhead.common.service.ServiceInformation;

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
            if (!ahProducer.isPublished()) {
                ahProducer.publish();
                ahProducers.add(ahProducer);
                System.out.println("\nNew Service Published \n - Name: "+ahProducer.getName()+"\n - Type: "+ahProducer.getServiceType()+"\n - URL:  "+ahProducer.getURL()+"\n - Path: "+ahProducer.getPath()+"\n - Port: "+ahProducer.getPort()+"\n");
            }
        } catch (ServiceRegisterException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            ahProducer.stop();
        }
    }
    
    private synchronized void removeProducer(AhProducer ahProducer) {
        if (ahProducer.isPublished()) {
            ahProducer.unpublish();
            ahProducers.remove(ahProducer);
            System.out.println("Service ["+ahProducer.getName()+"] is now unPublished.");
        } else {
            LOGGER.log(Level.WARNING, "Service {0} was not Published.", ahProducer.getName());
        }
    }
    
    public synchronized void removeProducer(String name) {
        AhProducer toRemove = null;
        for (AhProducer producer : ahProducers) {
            if (producer.getName().equals(name)) {
                toRemove = producer;
            }
        }
        if (toRemove!=null) removeProducer(toRemove);
    }
    
    private void removeAllProducers() {
        System.out.println("Removing All Producers...");
        List<String> toRemove = new ArrayList<>();
        for (AhProducer producer : ahProducers)
            toRemove.add(producer.getName());
        for (String producerName : toRemove)
            removeProducer(producerName);
    }
    
    public void printServiceDiscovery() {
        //AhServiceDiscovery ahSD = new AhServiceDiscovery(ahCore.getServiceDiscovery());
        //ahSD.execute(null, null);
        System.out.println("Service Discovery");
        List<ServiceIdentity> services = ahCore.getServiceDiscovery().getAllServices();
            for (ServiceIdentity service : services)
                System.out.println("Id:"+service.getId()+" Type:"+ service.getType());
        
    }
        
    public AhService findService(String serviceName) {
        AhService ahService = null;
        List<ServiceIdentity> services = ahCore.getServiceDiscovery().getAllServices();
        
        for (ServiceIdentity service: services) {
            String temp = service.getId();
            if (temp.subSequence(0, temp.indexOf(".")).equals(serviceName)) {
                ServiceInformation serviceInfo = ahCore.getServiceDiscovery().getServiceInformation(service, "HttpEndpoint");
                if (serviceInfo!=null) {
                    String tmp = serviceInfo.getEndpoint().toString();
                    String info = tmp.substring(tmp.indexOf("[")+1, tmp.lastIndexOf("]"));
                    String[] data = info.replaceAll("\\s","").split(",");
                    String host = null;
                    String port = null;
                    String path = null;
                    String secure = null;
                    for (String dat:data) {
                        String type = dat.substring(0, dat.indexOf("="));
                        switch (type) {
                            case "host":
                                host = dat.substring(type.length()+1);
                                break;
                            case "port":
                                port = dat.substring(type.length()+1);
                                break;
                            case "path":
                                path = dat.substring(type.length()+1);
                                break;
                            case "secure":
                                secure = dat.substring(type.length()+1);
                                break;
                            default: break;
                        }
                    }
                    ahService = new AhService(service.getId().substring(0, service.getId().indexOf(".")),
                            service.getType(),
                            host,
                            Integer.parseInt(port),
                            path,
                            Boolean.parseBoolean(secure));
                    
                }
            }
        }
        return ahService;
    }
    
}
