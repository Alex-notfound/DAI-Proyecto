package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.Map;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {

	private Socket socket;
	private Controller controller;

	public ServiceThread(Socket clientSocket, Controller controller) {
		this.socket = clientSocket;
		this.controller = controller;
	}

	// TODO: Si es POST o DELETE y envia "/" se muestra pagina Welcome o no?
	// TODO: Si es es POST o DELETE y envia "/html" se muestra listado?
	// TODO: Compruebo si contiene la clave uuid en GET y DELETE?
	@Override
	public void run() {

		Reader reader;
		HTTPResponse response = new HTTPResponse();
		try {
			reader = new InputStreamReader(socket.getInputStream());
			HTTPRequest request = new HTTPRequest(reader);

			switch (request.getMethod()) {
			case GET:
				if (request.getResourceChain().equals("/")) {
					response.setContent("Hybrid Server");
					response.setStatus(HTTPResponseStatus.S200);
				} else if (request.getResourceChain().contentEquals("/html")) {
					response.setContent(cargarListadoHtml());
					response.setStatus(HTTPResponseStatus.S200);
				} else {
					if (resourceNameValid(request.getResourceName())) {
						String uuid = request.getResourceParameters().get("uuid");
						if (this.controller.pageFound(uuid)) {
							response.setContent(this.controller.get(uuid));
							response.setStatus(HTTPResponseStatus.S200);
						} else {
							response.setContent("404 Not Found");
							response.setStatus(HTTPResponseStatus.S404);
						}
					} else {
						response.setContent("400 Bad Request");
						response.setStatus(HTTPResponseStatus.S400);
					}
				}
				break;
			case POST:
				// TODO: Sigo sin comprender el problema de resource en el content xxx="content"
				if (resourceNameValid(request.getResourceName())) {
					String uuid = this.controller
							.add(request.getContent().substring(request.getContent().indexOf('=') + 1));
					response.setContent(
							"<a href=\"" + request.getResourceName() + "?uuid=" + uuid + "\">" + uuid + "</a>");
					response.setStatus(HTTPResponseStatus.S200);
				} else {
					response.setContent("400 Bad Request");
					response.setStatus(HTTPResponseStatus.S400);
				}
				break;
			case DELETE:
				if (resourceNameValid(request.getResourceName())) {
					String uuid = request.getResourceParameters().get("uuid");
					if (this.controller.pageFound(uuid)) {
						this.controller.delete(uuid);
						response.setContent(this.controller.get(uuid));
						response.setStatus(HTTPResponseStatus.S200);
					} else {
						response.setContent("404 Not Found");
						response.setStatus(HTTPResponseStatus.S404);
					}
				} else {
					response.setContent("400 Bad Request");
					response.setStatus(HTTPResponseStatus.S400);
				}
				break;
			default:
				break;
			}

//			} else {
//				if (request.getMethod().equals(HTTPRequestMethod.POST)) {
//					// Reemplazar esto por parameters
//					String resource = request.getContent().substring(0, request.getContent().indexOf('='));
//					if (resource.equals("html")) {
//						String uuid = this.controller
//								.add(request.getContent().substring(request.getContent().indexOf('=') + 1));
//						response.setContent("<a href=\"" + resource + "?uuid=" + uuid + "\">" + uuid + "</a>");
//						response.setStatus(HTTPResponseStatus.S200);
//					} else {
//						response.setStatus(HTTPResponseStatus.S400);
//					}
//			}

		} catch (

		IOException e) {
			e.printStackTrace();
		} catch (HTTPParseException e) {
			e.printStackTrace();
		}
		response.setVersion("HTTP/1.1");

		Writer writer;
		try {
			writer = new OutputStreamWriter(socket.getOutputStream());
			response.print(writer);
			writer.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String cargarListadoHtml() {
		Map<String, String> allPages = this.controller.getAll();
		if (allPages.isEmpty()) {
			return "Hybrid Server";
		}
		String content = "";
		for (Map.Entry<String, String> entry : allPages.entrySet()) {
			content += "<a href=\\\"html?uuid=" + entry.getKey() + "\\\">" + entry.getKey() + "</a>";
		}
		return content;
	}

	private boolean resourceNameValid(String resourceName) {
		return resourceName.equals("html");
	}

	public void start() {
		this.run();
	}

}
