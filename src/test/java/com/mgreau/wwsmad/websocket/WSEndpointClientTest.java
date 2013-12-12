package com.mgreau.wwsmad.websocket;

import java.util.concurrent.CountDownLatch;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

/**
 * This class simulate actions done by the Browser with AngularJS.
 * 
 * @author @mgreau
 *
 */
@ClientEndpoint
public class WSEndpointClientTest {
	
	public static CountDownLatch latch = new CountDownLatch(2);
    public static String response;

    @OnOpen
    public void onOpen(Session session) {
    	System.out.println("Session open from: " + session.getRequestURI());
    	latch.countDown();
    }
    
    @OnMessage
    public void processMessage(String message) {
    	response = message;
        latch.countDown();
    }
}