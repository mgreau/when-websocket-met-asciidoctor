package com.mgreau.wildfly.websocket.messages;

public abstract class Message {
	
	/** Channel ID */
	protected String adocId;
	
	protected TypeMessage type;

	public TypeMessage getType() {
		return type;
	}

	public void setType(TypeMessage type) {
		this.type = type;
	}

	public String getAdocId() {
		return adocId;
	}

	public void setAdocId(String adocId) {
		this.adocId = adocId;
	}
	
	
    
}
