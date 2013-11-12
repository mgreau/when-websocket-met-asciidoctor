package com.mgreau.wildfly.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.mgreau.wildfly.StarterService;
import com.mgreau.wildfly.asciidoctor.AsciidoctorProcessor;
import com.mgreau.wildfly.websocket.decoders.MessageDecoder;
import com.mgreau.wildfly.websocket.encoders.AsciidocMessageEncoder;
import com.mgreau.wildfly.websocket.encoders.OutputMessageEncoder;
import com.mgreau.wildfly.websocket.messages.AsciidocMessage;
import com.mgreau.wildfly.websocket.messages.HTMLMessage;

/**
 * WSMAD : WebSocket met Asciidoctor !
 * 
 * @author @mgreau
 *
 */
@ServerEndpoint(
		value = "/adoc/{adoc-id}",
		        decoders = { MessageDecoder.class }, 
		        encoders = { AsciidocMessageEncoder.class, OutputMessageEncoder.class }
		)
public class WSMADEndpoint {
	
	/** log */
	private static final Logger logger = Logger.getLogger("WSMADEndpoint");
	
    /** All open WebSocket sessions */
    static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    
    /** Handle number of writers by adoc file */
    static Map<String, AtomicInteger> nbWritersByAdoc = new ConcurrentHashMap<>();
    
    @Inject StarterService ejbService;
    
    @Inject AsciidoctorProcessor processor;
    
    /**
     * Send Output Message resulted of an asciidoc source processor
     */
    public static void sendOutputMessage(HTMLMessage msg, String adocId) {
        try {
            /* Send updates to all open WebSocket sessions for this doc */
            for (Session session : peers) {
            	if (Boolean.TRUE.equals(session.getUserProperties().get(adocId))){
            		if (session.isOpen()){
	            		session.getBasicRemote().sendObject(msg);
	                    logger.log(Level.INFO, " HTML5 Sent : ", msg.toString());
            		}
            	}
            }
        } catch (IOException | EncodeException e) {
            logger.log(Level.INFO, e.toString());
        }   
    }
    
    @OnMessage
    public void message(final Session session, AsciidocMessage msg,  @PathParam("adoc-id") String adocId) {
        logger.log(Level.INFO, "Received: Asciidoc source from - {0}", msg);
        //check if the user had already bet and save this bet
        boolean isAlreadyAnAuhtor = session.getUserProperties().containsKey("isAuthor");
        session.getUserProperties().put("isAuthor", true);
        
        //check if there is any author
        if (!nbWritersByAdoc.containsKey(adocId)){
        	nbWritersByAdoc.put(adocId, new AtomicInteger());
        }
        if (!isAlreadyAnAuhtor){
        	nbWritersByAdoc.get(adocId).incrementAndGet();
        	
        }
        long start = System.currentTimeMillis();
        HTMLMessage html = new HTMLMessage(msg.getAuthor(), processor.renderAsDocument(msg.getAdocSource(), ""));
        html.setTimeToRender(System.currentTimeMillis() -  start);
        html.setNbWriters(nbWritersByAdoc.get(adocId).get());
        sendOutputMessage(html, adocId);
    }

    @OnOpen
    public void openConnection(Session session, @PathParam("adoc-id") String adocId) {
    	logger.log(Level.INFO, "Session ID : " + session.getId() +" - Connection opened for doc : " + adocId);
        session.getUserProperties().put(adocId, true);
        peers.add(session);
       
    }
    
    @OnClose
    public void closedConnection(Session session, @PathParam("adoc-id") String adocId) {
    	if (session.getUserProperties().containsKey("isAuthor")){
            /* Remove bet */
    		 nbWritersByAdoc.get(adocId).decrementAndGet();
    	}
        /* Remove this connection from the queue */
        peers.remove(session);
        logger.log(Level.INFO, "Connection closed.");
    }
    
    @OnError
    public void error(Session session, Throwable t) {
        peers.remove(session);
        logger.log(Level.INFO, t.toString());
        logger.log(Level.INFO, "Connection error.");
    }
   
}
