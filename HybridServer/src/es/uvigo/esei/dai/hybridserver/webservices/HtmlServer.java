package es.uvigo.esei.dai.hybridserver.webservices;

import javax.xml.ws.Endpoint;

public class HtmlServer {
	public static void main(String[] args) {
		Endpoint.publish("http://localhost:9876/calculus", new HtmlPageService());
	}
}
