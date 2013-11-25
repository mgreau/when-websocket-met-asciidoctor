package com.mgreau.wwsmad.websocket.messages;

import java.util.HashSet;
import java.util.Set;

public class NotificationMessage extends Message {

	/** people who send a version of the file */ 
	private Set<String> writers = new HashSet<String>();
	
	/** people who are connected but who don't send anything */
	private Integer nbConnected = 0;
	
	public NotificationMessage(){
		
	}

	public Set<String> getWriters() {
		return writers;
	}

	public void setWriters(Set<String> writers) {
		this.writers = writers;
	}

	public Integer getNbConnected() {
		return nbConnected;
	}

	public void setNbConnected(Integer nbConnected) {
		this.nbConnected = nbConnected;
	}
	

}
