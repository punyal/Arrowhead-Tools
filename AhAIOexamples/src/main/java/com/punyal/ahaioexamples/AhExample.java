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
package com.punyal.ahaioexamples;

import com.punyal.ahaio.AhAIO;
import com.punyal.ahaio.AhUtils;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@ltu.se>
 */
public class AhExample implements Runnable{
    private final AhAIO ahAIO;
    private final boolean running = true;
    private int counter;
    
    public AhExample() {
        counter = 0;
        System.out.println("My VPN IP: "+AhUtils.getVPNaddress());
        System.out.println("My URL: "+AhUtils.getVPNurlBnearIT());
        ahAIO = new AhAIO("./alpha.jks", "abc1234", "./alpha.jks", "abc1234", "./tsig");    
    }
    
    
    public static void main(String[] args) {
        System.out.println("Starting...");
        (new Thread(new AhExample())).start();
    }

    @Override
    public void run() {
        while (running) {
            counter++;
            System.out.format("[%3ds]\n",counter);
            
            // in 10 secs we publish a service
            if (counter == 10) ahAIO.addProducer("00Temp", "temp-ws-coap._udp", "/temperature00", AhUtils.getVPNurlBnearIT(), 5683, false);
            // in 20 secs we publish a service
            if (counter == 20) ahAIO.addProducer("01Temp", "temp-ws-coap._udp", "/temperature01", AhUtils.getVPNurlBnearIT(), 5683, false);

            // in 30 secs we publish a service
            if (counter == 30) ahAIO.printServiceDiscovery();

            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }   
}
