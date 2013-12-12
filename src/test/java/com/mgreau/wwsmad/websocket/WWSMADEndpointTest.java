package com.mgreau.wwsmad.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mgreau.wwsmad.StarterService;

@RunWith(Arquillian.class)
public class WWSMADEndpointTest {

	final String ADOC_URL = "adoc/";
	final String ADOC_ID = "121213";

	@ArquillianResource
	URI base;

	@Deployment(testable = false)
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class)
				.addPackages(true, StarterService.class.getPackage())
				.addAsManifestResource("MANIFEST.MF")
				.addAsWebInfResource("beans.xml");
	}
	
	@Test
	@InSequence(1)
	public void testNotificationOnOpenConnection() throws URISyntaxException,
			DeploymentException, IOException, InterruptedException {
		final String JSONNotificationOnOpen = "{\"type\":\"notification\",\"adocId\":\""+ ADOC_ID +"\",\"data\":{\"nbConnected\":1,\"nbWriters\":0,\"writers\":{}}}";
		Session session = connectToServer(WSEndpointClientTest.class,
				ADOC_URL+ADOC_ID);
		assertNotNull(session);

		assertTrue(WSEndpointClientTest.latch.await(2, TimeUnit.SECONDS));
		assertEquals(JSONNotificationOnOpen, WSEndpointClientTest.response);
	}
	
	/**
	 * WebSocket is not yet supported by default by Arquillian, so we need to change the
	 * schema manually.
	 * 
	 * @param endpoint
	 * @param uriPart
	 * @return
	 * @throws DeploymentException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Session connectToServer(Class<?> endpoint, String uriPart)
			throws DeploymentException, IOException, URISyntaxException {
		WebSocketContainer container = ContainerProvider
				.getWebSocketContainer();
		assertNotNull(container);
		assertNotNull(base);
		URI uri = new URI("ws://" + base.getHost() + ":" + base.getPort()
				+ base.getPath() + uriPart);
		System.out.println("Connecting to: " + uri);
		return container.connectToServer(endpoint, uri);
	}
}