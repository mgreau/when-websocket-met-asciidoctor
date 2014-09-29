package com.mgreau.wwsmad.websocket.messages;

import org.asciidoctor.ast.DocumentHeader;

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
	
	/** Asciidoc source from another user to merge with the adocSource */
	private String adocSourceToMerge;
	
	/** Patch to apply to source */
	private String patchToApply;

	/** Document format which is sent to peerŒ */
	protected TypeFormat format;
	
	/** Action to do with this message : render, compute diff, patch **/
	private String action;
	
	/** part of the message to render */
	private String part;
	
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

	public String getAdocSourceToMerge() {
		return adocSourceToMerge;
	}

	public void setAdocSourceToMerge(String adocSourceToMerge) {
		this.adocSourceToMerge = adocSourceToMerge;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPatchToApply() {
		return patchToApply;
	}

	public void setPatchToApply(String patchToApply) {
		this.patchToApply = patchToApply;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	
}
