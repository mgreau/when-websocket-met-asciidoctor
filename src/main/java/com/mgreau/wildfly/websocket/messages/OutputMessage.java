package com.mgreau.wildfly.websocket.messages;

/**
 * An OuputMessage is a result of :
 * 
 * The Asciidoctor processor parses an AsciiDoc document and translates it into
 * a variety of formats.
 * 
 * @author mgreau.com[Maxime Gréau]
 * 
 */
public class OutputMessage extends AsciidocMessage {
	
	/** time to process the file into output type */
	private long timeToRender;
	
	/** rendered by asciidoctor processor */
	private String content;
	
	public OutputMessage(TypeFormat format){
		this.format = format;
	}
	
	public String toString(){
		return "[OutputMessage][format]" + format + " - Rendered doc in: ..." + timeToRender;
	}
	
	public long getTimeToRender() {
		return timeToRender;
	}

	public void setTimeToRender(long timeToRender) {
		this.timeToRender = timeToRender;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
