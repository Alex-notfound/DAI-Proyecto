package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import es.uvigo.esei.dai.hybridserver.entity.Page;
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

	@Override
	public void run() {

		HTTPResponse response = new HTTPResponse();
		Reader reader;
		try {
			reader = new InputStreamReader(socket.getInputStream());
			HTTPRequest request = new HTTPRequest(reader);
			response.putParameter("Content-Type", "text/html");
			switch (request.getMethod()) {
			case GET:
				if (request.getResourceChain().equals("/")) {
					response.setContent("Hybrid Server\n\nAlexandre Currás Rodríguez");
					response.setStatus(HTTPResponseStatus.S200);
				} else if (request.getResourceChain().contentEquals("/html")) {
					response.setContent(cargarListadoHtml());
					response.setStatus(HTTPResponseStatus.S200);
				} else {
					if (resourceNameValid(request.getResourceName())) {
						String uuid = request.getResourceParameters().get("uuid");
						if (this.controller.pageFound(uuid)) {
							response.setContent(this.controller.get(uuid).getContent());
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
				if (resourceNameValid(request.getResourceName())
						&& request.getResourceParameters().containsKey(request.getResourceName())) {
					String uuid = this.controller.add(request.getResourceParameters().get(request.getResourceName()));
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
					if (uuid != null && this.controller.pageFound(uuid)) {
						response.setContent(this.controller.get(uuid).getContent());
						this.controller.delete(uuid);
						response.setStatus(HTTPResponseStatus.S200);
					} else {
						response.setContent("404 Not Found");
						response.setStatus(HTTPResponseStatus.S200);
					}
				} else {
					response.setContent("400 Bad Request");
					response.setStatus(HTTPResponseStatus.S400);
				}
				break;
			default:
				response.setContent("400 Bad Request");
				response.setStatus(HTTPResponseStatus.S400);
				break;
			}

		} catch (IOException e) {
			response.setContent("500 Internal Server Error");
			response.setStatus(HTTPResponseStatus.S500);
		} catch (HTTPParseException e) {
			response.setContent("500 Internal Server Error");
			response.setStatus(HTTPResponseStatus.S500);
		} catch (SQLException e) {
			response.setContent("500 Internal Server Error");
			response.setStatus(HTTPResponseStatus.S500);
		}
		response.setVersion("HTTP/1.1");

		try (Writer writer = new OutputStreamWriter(socket.getOutputStream())) {
			response.print(writer);
			// TODO: Preguntar si cerrar socketCliente aqui es correcto
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String cargarListadoHtml() throws SQLException {
		List<Page> allPages = this.controller.list();
		if (allPages.isEmpty()) {
			return "Hybrid Server";
		}
		// TODO: Preguntar si así está OK la pagina HTML
		String content = "<html><head></head><body>";
		for (Page page : allPages) {
			content += "<p><a href=\\html?uuid=" + page.getUuid() + ">" + page.getUuid() + "</a></p>";
		}
		content += "</body></html>";
		return content;
	}

	private boolean resourceNameValid(String resourceName) {
		return resourceName.equals("html");
	}

	public void start() {
		this.run();
	}

}
