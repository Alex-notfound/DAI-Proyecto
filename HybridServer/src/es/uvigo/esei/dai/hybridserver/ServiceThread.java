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
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {

	private Socket socket;
	private Controller controller;

	public ServiceThread(Socket clientSocket) {
		this.socket = clientSocket;
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
			System.out.println("Chegamos");
			reader = new InputStreamReader(socket.getInputStream());
			HTTPRequest request = new HTTPRequest(reader);
			if (request.getResourceParameters().containsKey("uuid")) {

				String uuid = request.getResourceParameters().get("uuid");
				if (!request.getResourceName().equals("html") && !request.getResourceName().equals("xml")
						&& !request.getResourceName().equals("xsd") && !request.getResourceName().equals("xslt")) {
					response.setContent("400 Bad Request");
					response.setStatus(HTTPResponseStatus.S400);
				} else if ((request.getMethod().equals(HTTPRequestMethod.GET)
						|| request.getMethod().equals(HTTPRequestMethod.DELETE)) && !this.controller.pageFound(uuid)) {
					response.setContent("404 Not Found");
					response.setStatus(HTTPResponseStatus.S404);
				} else {
					response.setContent(this.controller.get(uuid));
					response.setStatus(HTTPResponseStatus.S200);
				}
			} else {
				String content = "";
				Map<String, String> allPages = this.controller.getAll();
				for (Map.Entry<String, String> entry : allPages.entrySet()) {
					content += "<html><a src=>" + entry.getKey() + "</a></html>";
				}
				response.setContent(content);
			}
			response.setVersion("HTTP/1.1");
			System.out.println("Rematamos");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (HTTPParseException e) {
			e.printStackTrace();
		}

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

	public void start() {
		this.run();
	}

}
