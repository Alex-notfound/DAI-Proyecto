package es.uvigo.esei.dai.hybridserver.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import es.uvigo.esei.dai.hybridserver.Configuration;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;

public class SAXQuery extends DefaultHandler {

	private int httpPort;
	private int numClients;
	private String webServiceURL;
	private String dbUser;
	private String dbPassword;
	private String dbURL;
	private List<ServerConfiguration> servers = new ArrayList<>();

	private Configuration configuration;

	private boolean isHttp;
	private boolean isWebService;
	private boolean isNumClientes;
	private boolean isUrl;
	private boolean isPassword;
	private boolean isUser;

	public SAXQuery() {
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (localName) {
		case "http":
			isHttp = true;
			break;
		case "webservice":
			isWebService = true;
			break;
		case "numClients":
			isNumClientes = true;
			break;
		case "user":
			isUser = true;
			break;
		case "password":
			isPassword = true;
			break;
		case "url":
			isUrl = true;
			break;
		case "server":
			servers.add(new ServerConfiguration(attributes.getValue("name"), attributes.getValue("wsdl"),
					attributes.getValue("namespace"), attributes.getValue("service"),
					attributes.getValue("httpAddress")));
			break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String text = new String(ch, start, length);
		if (isHttp) {
			int num = Integer.parseInt(text);
			httpPort = num;
		} else if (isWebService) {
			webServiceURL = text;
		} else if (isNumClientes) {
			int num = Integer.parseInt(text);
			numClients = num;
		} else if (isUrl) {
			dbURL = text;
		} else if (isUser) {
			dbUser = text;
		} else if (isPassword) {
			dbPassword = text;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (localName) {
		case "http":
			isHttp = false;
			break;
		case "webservice":
			isWebService = false;
			break;
		case "numClients":
			isNumClientes = false;
			break;
		case "user":
			isUser = false;
			break;
		case "password":
			isPassword = false;
			break;
		case "url":
			isUrl = false;
			break;
		case "configuration":
			configuration = new Configuration(httpPort, numClients, webServiceURL, dbUser, dbPassword, dbURL, servers);
			break;
		}
	}
}
