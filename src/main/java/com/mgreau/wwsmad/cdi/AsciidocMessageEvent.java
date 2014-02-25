package com.mgreau.wwsmad.cdi;

import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.websocket.Session;

import com.mgreau.wwsmad.websocket.messages.AsciidocMessage;

@Named
public class AsciidocMessageEvent {
	
	 Session session;
	
	 AsciidocMessage msg;
	
	 String id;
	
	public AsciidocMessageEvent(@NotNull Session s, @NotNull String adocId, @NotNull AsciidocMessage msg){
		this.session = s;
		this.id = adocId;
		this.msg = msg;
	}
	
	
}
