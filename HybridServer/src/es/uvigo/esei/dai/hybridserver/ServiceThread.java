package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import es.uvigo.esei.dai.hybridserver.entity.Page;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;
import es.uvigo.esei.dai.hybridserver.parser.SAXParsing;

public class ServiceThread implements Runnable {

	private Socket socket;
	private Controller controller;

	public ServiceThread(Socket clientSocket, Controller controller) {
		this.socket = clientSocket;
		this.controller = controller;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {
			HTTPResponse response = new HTTPResponse();
			Reader reader;
			try {
				reader = new InputStreamReader(socket.getInputStream());
				HTTPRequest request = new HTTPRequest(reader);
				String resourceName = request.getResourceName();
				String resourceNameUpper = "HTML";
				if (resourceName != null) {
					resourceNameUpper = resourceName.toUpperCase();
				}
				String uuid = request.getResourceParameters().get("uuid");

				switch (request.getMethod()) {
				case GET:
					if (request.getResourceChain().equals("/")) {
						establecerContentType(request, response, "");
						ok200(response, "Hybrid Server\n\nAlexandre Currás Rodríguez");
					} else if (validResourceChain(request.getResourceChain())) {
						establecerContentType(request, response, resourceName);
						ok200(response, this.controller.list(resourceNameUpper));
					} else if (resourceName.equals("xml") && request.getResourceParameters().containsKey("xslt")) {
						establecerContentType(request, response, "html");
						getXMLWithXSLT(request, response);
					} else if (resourceNameValid(resourceName)) {
						if (this.controller.pageFound(uuid, resourceNameUpper)) {
							establecerContentType(request, response, resourceName);
							ok200(response, this.controller.get(uuid, resourceNameUpper).getContent());
						} else {
							notFound404(response);
						}
					} else {
						badRequest400(response);
					}
					break;
				case POST:
					if (resourceNameValid(resourceName) && request.getResourceParameters().containsKey(resourceName)) {
						if (resourceName.equals("xslt")) {
							if (request.getResourceParameters().containsKey("xsd")) {
								if (controller.pageFound(request.getResourceParameters().get("xsd"), "XSD")) {
									uuid = this.controller.add(request.getResourceParameters().get(resourceName),
											request.getResourceParameters().get("xsd"), resourceNameUpper);
									ok200(response,
											"<a href=\"" + resourceName + "?uuid=" + uuid + "\">" + uuid + "</a>");
								} else {
									notFound404(response);
								}
							} else {
								badRequest400(response);
							}
						} else {
							uuid = this.controller.add(request.getResourceParameters().get(resourceName), null,
									resourceNameUpper);
							ok200(response, "<a href=\"" + resourceName + "?uuid=" + uuid + "\">" + uuid + "</a>");
						}
					} else {
						badRequest400(response);
					}
					break;
				case DELETE:
					if (resourceNameValid(resourceName)) {
						if (uuid != null && this.controller.pageFound(uuid, resourceNameUpper)) {
							ok200(response, this.controller.get(uuid, resourceNameUpper).getContent());
							this.controller.delete(uuid, resourceNameUpper);
						} else {
							notFound404(response);
						}
					} else {
						badRequest400(response);
					}
					break;
				default:
					badRequest400(response);
					break;
				}

			} catch (IOException | HTTPParseException | SQLException e) {
				response.setContent("500 Internal Server Error");
				response.setStatus(HTTPResponseStatus.S500);
			} catch (ParserConfigurationException e) {
				notFound404(response);
			} catch (SAXException e) {
				badRequest400(response);
			} catch (TransformerException e) {
				System.err.println("TransformException");
			}
			response.setVersion("HTTP/1.1");

			Writer writer = new OutputStreamWriter(socket.getOutputStream());
			response.print(writer);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void getXMLWithXSLT(HTTPRequest request, HTTPResponse response)
			throws SQLException, ParserConfigurationException, SAXException, IOException, TransformerException {
		String uuid = request.getResourceParameters().get("uuid");
		if (!this.controller.pageFound(uuid, "XML")
				|| !this.controller.pageFound(request.getResourceParameters().get("xslt"), "XSLT")) {
			notFound404(response);
		} else {
			Page pageXSLT = this.controller.getXSLT(request.getResourceParameters().get("xslt"));
			String contentXML = this.controller.get(uuid, "XML").getContent();
			Reader readerXSD = new StringReader(this.controller.get(pageXSLT.getXsd(), "XSD").getContent());
			SAXParsing.parseAndValidateWithExternalXSD(new StringReader(contentXML), readerXSD, new DefaultHandler());
			ok200(response, transformWithXSLT(new StringReader(contentXML), new StringReader(pageXSLT.getContent())));
		}
	}

	private String transformWithXSLT(Reader f, Reader xslt) throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslt));
		StringWriter writer = new StringWriter();
		transformer.transform(new StreamSource(f), new StreamResult(writer));
		return writer.toString();
	}

	private boolean validResourceChain(String resourceChain) {
		return resourceChain.contentEquals("/html") || resourceChain.contentEquals("/xml")
				|| resourceChain.contentEquals("/xslt") || resourceChain.contentEquals("/xsd");
	}

	private void establecerContentType(HTTPRequest request, HTTPResponse response, String resourceName) {
		if (resourceName.equals("html") || request.getResourceParameters().isEmpty()) {
			response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
		} else {
			response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());
		}
	}

	private boolean resourceNameValid(String resourceName) {
		return resourceName.equals("html") || resourceName.equals("xml") || resourceName.equals("xslt")
				|| resourceName.equals("xsd");
	}

	public void start() {
		this.run();
	}

	private void badRequest400(HTTPResponse response) {
		response.setContent("400 Bad Request");
		response.setStatus(HTTPResponseStatus.S400);
	}

	private void notFound404(HTTPResponse response) {
		response.setContent("404 Not Found");
		response.setStatus(HTTPResponseStatus.S404);
	}

	private void ok200(HTTPResponse response, String content) {
		response.setContent(content);
		response.setStatus(HTTPResponseStatus.S200);
	}

}