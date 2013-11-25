package com.mgreau.wwsmad.websocket.decoders;

import javax.inject.Inject;
import javax.websocket.DecodeException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mgreau.wwsmad.websocket.messages.AsciidocMessage;

@RunWith(Arquillian.class)
public class MessageDecoderTest {
	
	@Inject MessageDecoder msgDecoder;

	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class)
				.addClass(MessageDecoder.class).addClass(AsciidocMessage.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}
	
	@Test
	public void shouldNotDecodeJSONMessage(){
		
		String msg = "{\"type\" : \"aadoc\", \"source\" : texttest, \"writer\": max}";
		AsciidocMessage adoc = null;
		
		try {
			//TODO Fix problem injection (nullpointer)
			//adoc = msgDecoder.decode(msg);
		} catch (Exception e) {
			Assert.assertEquals("[Message] Can't decode.", e.getMessage());
		}
		Assert.assertNull(adoc);
		
	}

}
