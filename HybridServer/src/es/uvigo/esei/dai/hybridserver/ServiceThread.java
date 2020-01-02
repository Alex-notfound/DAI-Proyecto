package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

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
				String resourceNameUpper = resourceName.toUpperCase();
				String uuid = request.getResourceParameters().get("uuid");

				switch (request.getMethod()) {
				case GET:
					if (request.getResourceChain().equals("/")) {
						establecerContentType(response, resourceName);
						ok200(response, "Hybrid Server\n\nAlexandre Currás Rodríguez");
					} else if (validResourceChain(request.getResourceChain())) {
						establecerContentType(response, resourceName);
						ok200(response, cargarListadoHtml(resourceNameUpper));
					} else if (resourceName.equals("xml") && request.getResourceParameters().containsKey("xslt")) {
						// TODO: Establecer ContentType ?
						getXMLWithXSLT(request, response);
					} else if (resourceNameValid(resourceName)) {
						if (this.controller.pageFound(uuid, resourceNameUpper)) {
							establecerContentType(response, resourceName);
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
							// TODO: No hay que guardar XSLT ?
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
							// TODO: Validate XML?
//							if (resourceName.equals("xml")) {
//								System.out.println(request.getContent());
							// SAXParsing.parseAndValidateWithExternalXSD(xmlPath, schemaPath, handler);
//							}
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
				System.err.println("TransFormException");
			}
			response.setVersion("HTTP/1.1");

			Writer writer = new OutputStreamWriter(socket.getOutputStream());
			response.print(writer);
		} catch (

		IOException e1) {
			e1.printStackTrace();
		}
	}

	private void getXMLWithXSLT(HTTPRequest request, HTTPResponse response)
			throws SQLException, ParserConfigurationException, SAXException, IOException, TransformerException {
		String uuid = request.getResourceParameters().get("uuid");
		// TODO: Validar y transformar
		if (this.controller.pageFound(uuid, "XML")
				&& !this.controller.pageFound(request.getResourceParameters().get("xslt"), "XSLT")) {
			notFound404(response);
		} else {
			File f = new File("newXML");
			try (PrintWriter out = new PrintWriter(f)) {
				out.print(this.controller.get(uuid, "XML").getContent());
				out.close();
			}
			// FIXME: Aiuda
			SAXParsing.parseAndValidateWithInternalXSD(f.getAbsolutePath(), new DefaultHandler());
			System.out.println("Validado con XSD interno");
			Page p = this.controller.get(request.getResourceParameters().get("xslt"), "XSLT");
			File xslt = new File("xslt");
			try (PrintWriter out = new PrintWriter(xslt)) {
				out.println(p.getContent());
				out.close();
			}
			File xsd = new File("xsd");
			try (PrintWriter out = new PrintWriter(xsd)) {
				out.println(this.controller.get(p.getXsd(), "XSD").getContent());
				out.close();
			}
			System.out.println("SECOND VALIDATE");
			// TODO: Validar con XSD del XSLT
			SAXParsing.parseAndValidateWithExternalXSD(f.getPath(), xsd.getPath(), new DefaultHandler());
			System.out.println("VALIDADO CON XSLT");
			ok200(response, transformWithXSLT(f, xslt));
		}
	}

	private String transformWithXSLT(File f, File xslt) throws TransformerException {
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

	private void establecerContentType(HTTPResponse response, String resourceName) {
		if (resourceName.equals("html")) {
			response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
		} else {
			response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());
		}

	}

	private String cargarListadoHtml(String resourceNameUpper) throws SQLException {
		List<Page> allPages = this.controller.list(resourceNameUpper);
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

	private void ok200(HTTPResponse response, String content) {
		response.setContent(content);
		response.setStatus(HTTPResponseStatus.S200);
	}

}