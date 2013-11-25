package com.mgreau.wwsmad.websocket.encoders;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.mgreau.wwsmad.websocket.messages.AsciidocMessage;

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
			builder.add("type", m.getType().toString())
					.add("adocId", m.getAdocId())
					.add("data",
							Json.createObjectBuilder()
									.add("format", m.getFormat().toString())
									.add("source", m.getAdocSource())
									.add("sourceToMerge", m.getAdocSourceToMerge()!=null?m.getAdocSourceToMerge():""));

			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}
}
