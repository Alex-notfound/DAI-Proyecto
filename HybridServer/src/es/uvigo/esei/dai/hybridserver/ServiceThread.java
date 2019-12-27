package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import es.uvigo.esei.dai.hybridserver.entity.Page;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

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
				controller.instantiateDao(request.getResourceName());
				String resourceName = request.getResourceName();

				switch (request.getMethod()) {
				case GET:
					if (request.getResourceChain().equals("/")) {
						establecerContentType(response, resourceName);
						response.setContent("Hybrid Server\n\nAlexandre Currás Rodríguez");
						response.setStatus(HTTPResponseStatus.S200);
					} else if (validResourceChain(request.getResourceChain())) {
						establecerContentType(response, resourceName);
						response.setContent(cargarListadoHtml());
						response.setStatus(HTTPResponseStatus.S200);
					} else if (resourceNameValid(resourceName)) {
						if (resourceName.equals("xml") && request.getResourceParameters().containsKey("xslt")) {
							// TODO: Validar y transformar
							// SAXParsing.parseAndValidateWithInternalXSD(xmlPath, handler);
							//transformWithXSLT(xmlSource, xsltSource, result);
						}
						establecerContentType(response, resourceName);
						String uuid = request.getResourceParameters().get("uuid");
						if (this.controller.pageFound(uuid)) {
							response.setContent(this.controller.get(uuid).getContent());
							response.setStatus(HTTPResponseStatus.S200);
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
								if (controller.XsdFound(request.getResourceParameters().get("xsd"))) {
									String uuid = this.controller.add(request.getResourceParameters().get(resourceName),
											request.getResourceParameters().get("xsd"));
									response.setContent(
											"<a href=\"" + resourceName + "?uuid=" + uuid + "\">" + uuid + "</a>");
									response.setStatus(HTTPResponseStatus.S200);
								} else {
									notFound404(response);
								}
							} else {
								badRequest400(response);
							}
						} else {
							if (resourceName.equals("xml")) {
								System.out.println(request.getContent());
								// TODO: Validate XML
								// SAXParsing.parseAndValidateWithExternalXSD(xmlPath, schemaPath, handler);
							}
							String uuid = this.controller.add(request.getResourceParameters().get(resourceName), null);
							response.setContent("<a href=\"" + resourceName + "?uuid=" + uuid + "\">" + uuid + "</a>");
							response.setStatus(HTTPResponseStatus.S200);
						}
					} else {
						badRequest400(response);
					}
					break;
				case DELETE:
					if (resourceNameValid(resourceName)) {
						String uuid = request.getResourceParameters().get("uuid");
						if (uuid != null && this.controller.pageFound(uuid)) {
							response.setContent(this.controller.get(uuid).getContent());
							this.controller.delete(uuid);
							response.setStatus(HTTPResponseStatus.S200);
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
			}

			response.setVersion("HTTP/1.1");

			Writer writer = new OutputStreamWriter(socket.getOutputStream());
			response.print(writer);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private boolean validResourceChain(String resourceChain) {
		return resourceChain.contentEquals("/html") || resourceChain.contentEquals("/xml")
				|| resourceChain.contentEquals("/xslt") || resourceChain.contentEquals("/xsd");
	}

	private void establecerContentType(HTTPResponse response, String resourceName) {
		if (resourceName.equals("html")) {
			response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
		} else {
			response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());
		}

	}

	private String cargarListadoHtml() throws SQLException {
		List<Page> allPages = this.controller.list();
		if (allPages.isEmpty()) {
			return "Hybrid Server";
		}
		String content = "<html><head></head><body>";
		for (Page page : allPages) {
			content += "<p><a href=\\html?uuid=" + page.getUuid() + ">" + page.getUuid() + "</a></p>";
		}
		content += "</body></html>";
		return content;
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

	public static void transformWithXSLT(Source xmlSource, Source xsltSource, Result result)
			throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(xsltSource);
		transformer.transform(xmlSource, result);
	}
}