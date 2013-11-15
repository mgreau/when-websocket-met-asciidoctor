package com.mgreau.wildfly.websocket.messages;

import java.util.ArrayList;
import java.util.Collection;

import org.asciidoctor.DocumentHeader;

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
	
	/** The current person who send this version */
	private String currentWriter;
	
	/** Asciidoc file information */
	private DocumentHeader docHeader;
	
	/** Asciidoc source origin content */
	private String adocSource;

	/** Document format which is sent to peerŒ */
	protected TypeFormat format;
	
	public AsciidocMessage(){
	}
	
	public AsciidocMessage(String currentWriter, String adocSource){
		this.format = TypeFormat.asciidoc;
		this.currentWriter = currentWriter;
		this.adocSource = adocSource;
	}
	
	public AsciidocMessage(String currentWriter, String adocSource, TypeFormat format){
		this.format = TypeFormat.asciidoc;
		if (format !=  null)
			this.format = format;
		this.currentWriter = currentWriter;
		this.adocSource = adocSource;
	}
	
	
	public String toString(){
		return "[AsciidocMessage]" + " - By writer: ..." + currentWriter;
	}

	public String getCurrentWriter() {
		return currentWriter;
	}

	public void setCurrentWriter(String currentWriter) {
		this.currentWriter = currentWriter;
	}

	public String getAdocSource() {
		return adocSource;
	}


	public void setAdocSource(String adocSource) {
		this.adocSource = adocSource;
	}


	public DocumentHeader getDocHeader() {
		return docHeader;
	}


	public void setDocHeader(DocumentHeader docHeader) {
		this.docHeader = docHeader;
	}

	public TypeFormat getFormat() {
		return format;
	}

	public void setFormat(TypeFormat format) {
		this.format = format;
	}

	
}
