package com.mgreau.wildfly.websocket.messages;

import java.util.ArrayList;
import java.util.Collection;


public class HTMLMessage extends Message {

	/** Author for this adoc version */
	private String author;
	
	/** html source content */
	private String htmlSource;
	
	/** time to process the file into html*/
	private long timeToRender;
	
	/** Number of writers on this doc */
	private Integer nbWriters;
	
	private Collection<String> authors = new ArrayList<String>();
	
	private Collection<String> connected = new ArrayList<String>();
	
	
	public String toString(){
		return "[HTMLMessage][author]" + author + " - nb Writers : " + nbWriters +" - Rendered doc in: ..." + timeToRender;
	}
	

	public HTMLMessage(String author, String htmlSource) {
		super();
		this.author = author;
		this.htmlSource = htmlSource;
		this.timeToRender = 0;
		this.nbWriters = 0;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getHtmlSource() {
		return htmlSource;
	}

	public void setHtmlSource(String htmlSource) {
		this.htmlSource = htmlSource;
	}

	public long getTimeToRender() {
		return timeToRender;
	}

	public void setTimeToRender(long timeToRender) {
		this.timeToRender = timeToRender;
	}
	

	public Integer getNbWriters() {
		return nbWriters;
	}


	public void setNbWriters(Integer nbWriters) {
		this.nbWriters = nbWriters;
	}
	

}
