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

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@ltu.se>
 */
public class AhService {
    private final String name;
    private final String type;
    private final String host;
    private final int port;
    private final String path;
    private final boolean secure;
    
    public AhService(String name, String type, String host, int port, String path, boolean secure) {
        this.name = name;
        this.type = type;
        this.host = host;
        this.port = port;
        this.path = path;
        this.secure =secure;
    }
        
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getPath() {
        return path;
    }
    
    public boolean isSecure() {
        return secure;
    }
    
    @Override
    public String toString() {
        return "Service--------"
        + "\n -Name: "+name
        + "\n -Type: "+type
        + "\n -Host: "+host
        + "\n -Path: "+path
        + "\n -Port: "+port
        + "\n -Secure: "+secure
        + "\n----------------------";
    }
    
    public boolean isCoap() {
        return type.toLowerCase().contains("coap");
    }
    
    public String getCoapURI() {
        if (isCoap()) {
            return "coap://"+host+":"+port+path;
        } else
            return null;
    }
}