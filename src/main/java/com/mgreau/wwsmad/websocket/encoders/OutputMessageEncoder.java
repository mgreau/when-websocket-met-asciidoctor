package com.mgreau.wwsmad.websocket.encoders;

import java.io.StringWriter;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.mgreau.wwsmad.websocket.messages.OutputMessage;

public class OutputMessageEncoder implements Encoder.Text<OutputMessage> {

	private static final Logger logger = Logger
			.getLogger("OutputMessageEncoder");

	@Override
	public void init(EndpointConfig ec) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public String encode(OutputMessage m) {
		StringWriter swriter = new StringWriter();
		try {
			try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
				JsonObjectBuilder builder = Json.createObjectBuilder();
				builder.add("type", m.getType().toString())
						.add("adocId", m.getAdocId())
						.add("data",
								Json.createObjectBuilder()
										.add("format", m.getFormat().toString())
										.add("currentWriter",
												m.getCurrentWriter())
										.add("docHeader",
												Json.createObjectBuilder()
														.add("title",
																m.getDocHeader()
																		.getDocumentTitle())
														.add("author", m.getDocHeader().getAuthor().getFullName())
														.add("revisioninfo",
																m.getDocHeader()
																		.getRevisionInfo()
																		.getNumber()))
										.add("source", m.getAdocSource())
										.add("timeToRender",
												m.getTimeToRender())
										.add("output", m.getContent()));

				jsonWrite.writeObject(builder.build());
			}
		} catch (Exception e) {
			logger.severe("Output message decode error.");
			Json.createWriter(swriter).writeObject(getDefault(m).build());
		}
		return swriter.toString();
	}

	private JsonObjectBuilder getDefault(OutputMessage m) {

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", m.getType().toString())
				.add("adocId", m.getAdocId())
				.add("data",
						Json.createObjectBuilder().add("format", "")
								.add("currentWriter", "")
								.add("docHeader", Json.createObjectBuilder())
								.add("source", m.getAdocSource()).add("timeToRender", "-1")
								.add("output", "<b>Error in Asciidoc source !</b> <br/>Check the following and re-try : "
										+ "<ul><li>Headers informations are mandatory (title, author name, revision, date)</li></lu>"));
		return builder;

	}
}
