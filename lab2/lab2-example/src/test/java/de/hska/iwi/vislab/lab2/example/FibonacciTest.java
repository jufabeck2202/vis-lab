
package de.hska.iwi.vislab.lab2.example;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;

public class FibonacciTest {

	private HttpServer server;
	private WebTarget target;

	@Before
	public void setUp() throws Exception {
		// start the server
		server = Main.startServer();
		// create the client
		Client c = ClientBuilder.newClient();

		// uncomment the following line if you want to enable
		// support for JSON in the client (you also have to uncomment
		// dependency on jersey-media-json module in pom.xml and
		// Main.startServer())
		// --
		// c.configuration().enable(new
		// org.glassfish.jersey.media.json.JsonJaxbFeature());

		target = c.target(Main.BASE_URI);
	}

	@After
	public void tearDown() throws Exception {
		server.shutdown();
	}

	@Test
	public void getPlain() {
		//Reset for test
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).delete();

		assertEquals("0", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));
	}

	@Test
	public void getHTML() {
		//Reset for test
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).delete();
		assertEquals("<html><title>Fibonacci</title><body><h2>Fibonacci: 0</h2></body></html>", target.path("fibonacci").request().accept(MediaType.TEXT_HTML).get(String.class));
	}

	@Test
	public void getJson() {
		//Reset for test
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).delete();

		assertEquals("{\"fibonacci\": 0}", target.path("fibonacci").request().accept(MediaType.APPLICATION_JSON).get(String.class));
	}

	@Test
	public void increment() {
		//Reset for test
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).delete();
		assertEquals("0", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));

		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).post(Entity.json(""));
		assertEquals("1", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));

		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).post(Entity.json(""));
		assertEquals("1", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));

		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).post(Entity.json(""));
		assertEquals("2", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));

		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).post(Entity.json(""));
		assertEquals("3", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));

		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).post(Entity.json(""));
		assertEquals("5", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));

		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).post(Entity.json(""));
		assertEquals("8", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));

		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).post(Entity.json(""));
		assertEquals("13", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));

		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).post(Entity.json(""));
		assertEquals("21", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));

		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).post(Entity.json(""));
		assertEquals("34", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));
		//Test Delete Again
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).delete();
		assertEquals("0", target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).get(String.class));
	}

}
