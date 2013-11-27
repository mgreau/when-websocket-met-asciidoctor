package com.mgreau.wwsmad.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WWSMADEndpointTest {

    private static final String RESPONSE = "Hello World!";

    @ArquillianResource
    URI base;

    /**
     * Arquillian specific method for creating a file which can be deployed
     * while executing the test.
     */
    @Deployment(testable=false)
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addPackage(WWSMADEndpoint.class.getPackage());
        System.out.println(war.toString(true));
        return war;
    }

    /**
     * The basic test method for the class {@link WWSMADEndpoint}
     *
     *
     * @throws DeploymentException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testNotificationOnOpen() throws DeploymentException, IOException, URISyntaxException, InterruptedException {
        Session session = connectToServer("1234");
        assertNotNull(session);
        System.out.println("Waiting for 2 seconds to receive response");
        Thread.sleep(2000);
        assertNotNull(WSEndpointClientTest.response);
        assertEquals(RESPONSE, WSEndpointClientTest.response);
    }

   

    /**
     * Method used to supply connection to the server by passing the naming of
     * the websocket endpoint
     *
     * @param endpoint
     * @return
     * @throws DeploymentException
     * @throws IOException
     * @throws URISyntaxException
     */
    public Session connectToServer(String endpoint) throws DeploymentException, IOException, URISyntaxException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = new URI("ws://"
                        + base.getHost()
                        + ":"
                        + base.getPort()
                        + "/"
                        + base.getPath()
                        + "/adoc/"

                        + endpoint);
        System.out.println("Connecting to: " + uri);
        return container.connectToServer(WSEndpointClientTest.class, uri);
                
    }
}