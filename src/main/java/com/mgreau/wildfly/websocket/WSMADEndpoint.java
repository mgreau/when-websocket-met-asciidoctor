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

import com.mgreau.wildfly.asciidoctor.AsciidoctorProcessor;
import com.mgreau.wildfly.websocket.decoders.MessageDecoder;
import com.mgreau.wildfly.websocket.encoders.AsciidocMessageEncoder;
import com.mgreau.wildfly.websocket.encoders.NotificationMessageEncoder;
import com.mgreau.wildfly.websocket.encoders.OutputMessageEncoder;
import com.mgreau.wildfly.websocket.messages.AsciidocMessage;
import com.mgreau.wildfly.websocket.messages.NotificationMessage;
import com.mgreau.wildfly.websocket.messages.OutputMessage;
import com.mgreau.wildfly.websocket.messages.TypeFormat;
import com.mgreau.wildfly.websocket.messages.TypeMessage;

/**
 * WSMAD : WebSocket met Asciidoctor !
 * 
 * @author greau.maxime@gmail.com
 * 
 */
@ServerEndpoint(value = "/adoc/{adoc-id}", decoders = { MessageDecoder.class }, encoders = {
		AsciidocMessageEncoder.class, OutputMessageEncoder.class,
		NotificationMessageEncoder.class })
public class WSMADEndpoint {

	private static final Logger logger = Logger.getLogger("WSMADEndpoint");

	/** All open WebSocket sessions */
	static Set<Session> peers = Collections
			.synchronizedSet(new HashSet<Session>());

	/** Handle number of readers by adoc file */
	static Map<String, AtomicInteger> nbReadersByAdoc = new ConcurrentHashMap<>();

	/** Handle number of writers by adoc file */
	static Map<String, Set<String>> writersByAdoc = new ConcurrentHashMap<String, Set<String>>();

	@Inject
	AsciidoctorProcessor processor;

	/**
	 * Send, to all peers connected to this asciidoc file, the Output Message
	 * resulted of an asciidoc source processor.
	 * 
	 * @param msg
	 *            OutputMessage
	 * @param adocId
	 *            unique id for this asciidoc file
	 */
	public static void sendOutputMessage(OutputMessage msg, String adocId) {
		NotificationMessage nfMsg = new NotificationMessage();
		nfMsg.setNbConnected(nbReadersByAdoc.get(adocId).get());
		nfMsg.setAdocId(adocId);
		nfMsg.setType(TypeMessage.notification);
		
		try {
			for (Session session : peers) {
				if (Boolean.TRUE
						.equals(session.getUserProperties().get(adocId))) {
					if (session.isOpen()) {
						session.getBasicRemote().sendObject(msg);
						logger.log(Level.INFO, " Outpout Sent : ",
								msg.toString());
						sendNotificationMessage(session, nfMsg, adocId);
					}
				}
			}
		} catch (IOException | EncodeException e) {
			logger.log(Level.SEVERE, e.getCause().toString());
		}
	}

	/**
	 * Send a notification to one peer define by the session
	 * 
	 * @param session
	 * @param msg
	 * @param adocId
	 */
	public static void sendNotificationMessage(Session session,
			NotificationMessage msg, String adocId) {
		try {
			msg.getWriters().addAll(writersByAdoc.get(adocId));
			session.getBasicRemote().sendObject(msg);
			logger.log(Level.INFO, "Notification Sent: {0}", msg.toString());
		} catch (IOException | EncodeException e) {
			logger.log(Level.SEVERE, e.toString());
		}
	}

	/**
	 * Send notifications to all connected peers.
	 * 
	 * @param msg
	 * @param adocId
	 */
	public static void sendNotificationMessage(NotificationMessage msg,
			String adocId) {
		for (Session session : peers) {
			if (Boolean.TRUE.equals(session.getUserProperties().get(adocId))) {
				if (session.isOpen()) {
					sendNotificationMessage(session, msg, adocId);
				}
			}
		}
	}

	/**
	 * Received a AsciidocMessage from one peer with a new version of the source
	 * file.
	 * 
	 * @param session
	 *            peer who send a new version
	 * @param msg
	 *            the AsciidocMessage
	 * @param adocId
	 *            unique id for this asciidoc file
	 */
	@OnMessage
	public void message(final Session session, AsciidocMessage msg,
			@PathParam("adoc-id") String adocId) {
		logger.log(Level.INFO, "Received: Asciidoc source from - {0}", msg);

		// check if the user had already send a version for this doc
		boolean wasAlreadyAnAuhtor = session.getUserProperties().containsKey(
				"writer");
		if (!wasAlreadyAnAuhtor) {
			session.getUserProperties().put("writer", msg.getCurrentWriter());
			handleReaders(adocId, false);
			handleWriters(adocId, true, msg.getCurrentWriter());
		} else {
			// TODO handle if the name is modified
		}

		OutputMessage html = new OutputMessage(TypeFormat.html5);
		html.setAdocId(adocId);
		html.setType(TypeMessage.output);
		html.setCurrentWriter(msg.getCurrentWriter());
		long start = System.currentTimeMillis();
		html.setTimeToRender(System.currentTimeMillis() - start);
		try {
			html.setContent(processor.renderAsDocument(msg.getAdocSource(), ""));
			html.setDocHeader(processor.renderDocumentHeader(msg
					.getAdocSource()));
		} catch (RuntimeException rEx) {
			logger.severe("processing error." + rEx.getCause().toString());
		}
		html.setAdocSource(msg.getAdocSource());

		// send the new HTML version to all connected peers
		sendOutputMessage(html, adocId);
	}

	@OnOpen
	public void openConnection(Session session,
			@PathParam("adoc-id") String adocId) {
		logger.log(Level.INFO, "Session ID : " + session.getId()
				+ " - Connection opened for doc : " + adocId);
		session.getUserProperties().put(adocId, true);
		peers.add(session);
		// send a message to all peers to inform that someone is connected
		handleReaders(adocId, true);
		if (!writersByAdoc.containsKey(adocId)) {
			writersByAdoc.put(adocId, new HashSet<String>());
		}

		sendNotificationMessage(createNotification(adocId), adocId);
	}

	/**
	 * Close the connection and decrement the number of writers and send a
	 * message to notify all others writers.
	 * 
	 * @param session
	 *            peer session
	 * @param adocId
	 *            unique id for this asciidoc file
	 */
	@OnClose
	public void closedConnection(Session session,
			@PathParam("adoc-id") String adocId) {
		if (session.getUserProperties().containsKey("writer")) {
			handleWriters(adocId, false, (String) session.getUserProperties()
					.get("writer"));
		} else {
			handleReaders(adocId, false);
		}

		peers.remove(session);
		logger.log(Level.INFO, "Connection closed for " + session.getId());
		// send a message to all peers to inform that someone is disonnected
		sendNotificationMessage(createNotification(adocId), adocId);
	}

	@OnError
	public void error(Session session, Throwable t) {
		//TODO send a notification to alert user
		logger.log(Level.SEVERE, t.toString());
		logger.log(Level.SEVERE, "Connection error!");
	}

	private void handleReaders(String adocId, boolean isPlus) {
		// check if there is any author for this id
		if (!nbReadersByAdoc.containsKey(adocId)) {
			nbReadersByAdoc.put(adocId, new AtomicInteger());
		}
		if (isPlus)
			nbReadersByAdoc.get(adocId).incrementAndGet();
		else
			nbReadersByAdoc.get(adocId).decrementAndGet();
	}

	private void handleWriters(String adocId, boolean isPlus, String writer) {
		if (!writersByAdoc.containsKey(adocId)) {
			writersByAdoc.put(adocId, new HashSet<String>());
		}
		if (isPlus)
			writersByAdoc.get(adocId).add(writer);
		else
			writersByAdoc.get(adocId).remove(writer);
	}

	private NotificationMessage createNotification(String adocId) {
		NotificationMessage nfMsg = new NotificationMessage();
		nfMsg.setNbConnected(nbReadersByAdoc.get(adocId).get());
		nfMsg.setAdocId(adocId);
		nfMsg.setType(TypeMessage.notification);
		return nfMsg;
	}

}
