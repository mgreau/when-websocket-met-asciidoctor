package com.mgreau.wildfly.websocket.encoders;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.mgreau.wildfly.websocket.messages.OutputMessage;

public class OutputMessageEncoder implements Encoder.Text<OutputMessage> {
	
	@Override
	public void init(EndpointConfig ec) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public String encode(OutputMessage m) throws EncodeException {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("type", m.getType().toString())
			.add("adocId", m.getAdocId())
			.add("data",
					Json.createObjectBuilder()
							.add("format", m.getFormat().toString())
							.add("currentWriter", m.getCurrentWriter())
							.add("docHeader",
									Json.createObjectBuilder()
											.add("title",
													m.getDocHeader()
															.getDocumentTitle())
											.add("author",
													m.getDocHeader().getAuthor().getFullName())
											.add("revisioninfo",
													m.getDocHeader()
															.getRevisionInfo().getNumber()))
							.add("source", m.getAdocSource())
							.add("timeToRender", m.getTimeToRender())
							.add("output", m.getContent()));

			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}
}
