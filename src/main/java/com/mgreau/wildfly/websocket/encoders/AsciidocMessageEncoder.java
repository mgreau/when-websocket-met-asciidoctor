package com.mgreau.wildfly.websocket.encoders;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.mgreau.wildfly.websocket.messages.AsciidocMessage;

public class AsciidocMessageEncoder implements Encoder.Text<AsciidocMessage> {
	@Override
	public void init(EndpointConfig ec) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public String encode(AsciidocMessage m) throws EncodeException {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add(
					"adoc",
					Json.createObjectBuilder()
					.add("author", m.getAuthor())
					.add("source", m.getAdocSource()));

			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}
}
