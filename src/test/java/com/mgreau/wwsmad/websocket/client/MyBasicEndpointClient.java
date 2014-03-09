package com.mgreau.wwsmad.websocket.client;

import java.util.concurrent.CountDownLatch;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
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
public class MyBasicEndpointClient {
	
	public static CountDownLatch latch;
    public static String notificationMessage;
    public static String outputMessage;
    public static String adocMessage;

    @OnOpen
    public void onOpen(Session session) {
    	System.out.println("Session open from: " + session.getRequestURI());
    	latch.countDown();
    }
    
    @OnMessage
    public void processMessage(String message) {
    	latch.countDown();
    	if (message.contains("type\":\"output"))
    		outputMessage = message;
    	else if (message.contains("type\":\"adoc-for-html5"))
    		adocMessage = message;
    	else
    		notificationMessage = message;
    	System.out.println("Message received: " + message.toString());
        
    }
    
    public String getNotificationMessage(){
    	return notificationMessage;
    }
    
    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }
    
    
}