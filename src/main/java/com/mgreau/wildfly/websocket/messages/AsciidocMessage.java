package com.mgreau.wildfly.websocket.messages;

/**
 * This message can be send by both peers (client/server) :
 * <ul>
 * <li>by peer client : when a writer wants to see how his doc looks like in HTML</li>
 * <li>by peer server : 
 * 	<ul>
 * 		<li>if client want to have the last snapshot to work on it</li>
 * 		<li>each time an other writer send a new version if the client is connected to live editing</li>
 * 	</ul>
 * </li>
 * </ul>
 * 
 * @author @mgreau
 *
 */
public class AsciidocMessage extends Message {
	
	/** Author for this adoc version */
	private String author;
	
	/** Asciidoc source content */
	private String adocSource;
	
	
	public AsciidocMessage(String author, String adoc){
		this.author = author;
		this.adocSource = adoc;
	}
	
	
	public String toString(){
		return "[AsciidocMessage]" + " - Last Author: ..." + author;
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getAdocSource() {
		return adocSource;
	}


	public void setAdocSource(String adocSource) {
		this.adocSource = adocSource;
	}

	
}
