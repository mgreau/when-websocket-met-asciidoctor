package com.mgreau.wwsmad.websocket.decoders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mgreau.wwsmad.cdi.LoggerProducer;
import com.mgreau.wwsmad.websocket.messages.AsciidocMessage;
import com.mgreau.wwsmad.websocket.messages.Message;

@RunWith(Arquillian.class)
public class MessageDecoderTest {

	@Deployment
	public static JavaArchive createDeployment() {
		return ShrinkWrap
				.create(JavaArchive.class, "MessageDecoderTest.jar")
				.addClasses(MessageDecoder.class, LoggerProducer.class)
				.addPackage(Message.class.getPackage())
				.addAsManifestResource("beans.xml");
	}

	@Inject
	MessageDecoder msgDecoder;

	@Test
	public void shouldNotDecodeJSONMessage() {
		assertNotNull(msgDecoder);

		final String JSON = "{\"type\" : \"aadoc\", \"sourrrce\" : \"texttest\", \"writer\": \"max\"}";
		AsciidocMessage adoc = null;

		try {
			adoc = msgDecoder.decode(JSON);
		} catch (Exception e) {
			assertEquals("[Message] Can't decode.", e.getMessage());
		}
		assertNull(adoc);
	}
}
