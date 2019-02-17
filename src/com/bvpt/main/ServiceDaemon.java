/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bvpt.main;

import com.bvpt.common.Config;
import org.apache.log4j.Logger;

/**
 *
 * @author tamvh
 */
public class ServiceDaemon {
    
    private static final String DEFAULT_CONFIGURATION_FILE = "bvptcom.conf";
    private static final Logger logger = Logger.getLogger(ServiceDaemon.class);
    private static WebServer webServer = null;
    
    public static void main(String[] args) {
        try {
            Config.init(DEFAULT_CONFIGURATION_FILE);
            
            webServer = WebServer.getInstance();
            new Thread(webServer).start();
             
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.info("Shutdown thread before webserver getinstance");
                        if (webServer != null) {
                            webServer.stop();
                        }
                    } catch (Exception e) {
                    }
                }
            }, "Stop Jetty Hook"));
        } catch (Throwable e) {
            String msg = "Exception encountered during startup.";
            logger.error(msg, e);
            System.out.println(msg);
            logger.error("Uncaught exception: " + e.getMessage(),e);
            System.exit(3);
        }
    }
}
