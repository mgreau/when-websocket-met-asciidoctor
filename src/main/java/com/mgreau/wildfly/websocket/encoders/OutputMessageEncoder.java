package com.mgreau.wildfly.websocket.encoders;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.mgreau.wildfly.websocket.messages.AsciidocMessage;
import com.mgreau.wildfly.websocket.messages.HTMLMessage;

public class OutputMessageEncoder implements Encoder.Text<HTMLMessage> {
	
	@Override
	public void init(EndpointConfig ec) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public String encode(HTMLMessage htmlMsg) throws EncodeException {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add(
					"html5Backend",
					Json.createObjectBuilder()
					.add("author", htmlMsg.getAuthor())
					.add("source", htmlMsg.getHtmlSource())
					.add("nbWriters", Long.toString(htmlMsg.getNbWriters()))
					.add("timeToRender", Long.toString(htmlMsg.getTimeToRender())));

			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}
}
