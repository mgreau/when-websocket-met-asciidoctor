package com.mgreau.wwsmad.websocket;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

/**
 * This class simulate actions done by AngularJS.
 * 
 * @author mgreau
 *
 */
@ClientEndpoint
public class WSEndpointClientTest {
    public static String response;

    @OnOpen
    public void onOpen(Session session) {
        
    }
    
    @OnMessage
    public void processMessage(String message) {
    	response = message;
        System.out.println(message);
    }
}