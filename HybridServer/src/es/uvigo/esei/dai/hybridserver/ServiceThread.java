package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {

	private Socket socket;
	private Controller controller;

	public ServiceThread(Socket clientSocket) {
		this.socket = clientSocket;
		this.controller = new Controller(new MemoryDAO(new HashMap<String, String>()));
	}

	public ServiceThread(Socket clientSocket, Controller controller) {
		this.socket = clientSocket;
		this.controller = controller;
	}

	@Override
	public void run() {

		Reader reader;
		HTTPResponse response = new HTTPResponse();
		try {
			reader = new InputStreamReader(socket.getInputStream());
			HTTPRequest request = new HTTPRequest(reader);

			// TODO: Preguntar orden de comprobaciones
			if (request.getResourceParameters().containsKey("uuid")) {
				String uuid = request.getResourceParameters().get("uuid");
				if (!goodRequest(request.getResourceName())) {
					response.setContent("400 Bad Request");
					response.setStatus(HTTPResponseStatus.S400);
				} else if ((request.getMethod().equals(HTTPRequestMethod.GET)
						|| request.getMethod().equals(HTTPRequestMethod.DELETE)) && !this.controller.pageFound(uuid)) {
					response.setContent("404 Not Found");
					response.setStatus(HTTPResponseStatus.S404);
				} else {
					response.setContent(this.controller.get(uuid));
					response.setStatus(HTTPResponseStatus.S200);
					if (request.getMethod().equals(HTTPRequestMethod.DELETE)) {
						this.controller.delete(uuid);
					}
				}
			} else {
				if (request.getMethod().equals(HTTPRequestMethod.POST)) {

					String resource = request.getContent().substring(0, request.getContent().indexOf('='));
					if (goodRequest(resource)) {
						String uuid = this.controller
								.add(request.getContent().substring(request.getContent().indexOf('=') + 1));
						response.setContent("<a href=\"" + resource + "?uuid=" + uuid + "\">" + uuid + "</a>");
						response.setStatus(HTTPResponseStatus.S200);
					} else {
						response.setStatus(HTTPResponseStatus.S400);
					}
					// TODO: Preguntar si esto se aplica a cualquier metodo que no sea POST
				} else {
					Map<String, String> allPages = this.controller.getAll();
					if (allPages.isEmpty()) {
						response.setContent("Hybrid Server");
					} else {
						String content = "";
						for (Map.Entry<String, String> entry : allPages.entrySet()) {
							content += "<a href=\\\"html?uuid=" + entry.getKey() + "\\\">" + entry.getKey() + "</a>";
						}
						response.setContent(content);
					}
					response.setStatus(HTTPResponseStatus.S200);
				}
			}

		} catch (IOException e) {
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

	private boolean goodRequest(String resource) {
		return resource.equals("html") || resource.equals("xml") || resource.equals("xsd") || resource.equals("xslt");
	}

	public void start() {
		this.run();
	}

}
