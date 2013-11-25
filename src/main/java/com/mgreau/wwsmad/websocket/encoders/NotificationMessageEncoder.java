package com.mgreau.wwsmad.websocket.encoders;

import java.io.StringWriter;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.mgreau.wwsmad.websocket.messages.NotificationMessage;

public class NotificationMessageEncoder implements Encoder.Text<NotificationMessage> {
	@Override
	public void init(EndpointConfig ec) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public String encode(NotificationMessage m) throws EncodeException {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("type", m.getType().toString()).
				add("adocId", m.getAdocId()).
				add(
					"data",
					Json.createObjectBuilder()
					.add("nbConnected", m.getNbConnected())
					 .add("nbWriters", m.getWriters().size())
					.add("writers", toJSON(m.getWriters())));

			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}
	
	private JsonObjectBuilder toJSON(Set<String> writers){
		JsonObjectBuilder jsb = Json.createObjectBuilder();
		for(String writer : writers){
			jsb.add(writer,writer);
		}
		return jsb;
		
	}
}
