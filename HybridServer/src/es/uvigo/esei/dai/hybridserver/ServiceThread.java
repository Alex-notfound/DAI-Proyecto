package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {

	private Socket socket;

	public ServiceThread(Socket clientSocket) {
		this.socket = clientSocket;
	}

	@Override
	public void run() {
		// TODO: Dar respuesta
		HTTPResponse response = new HTTPResponse();
		response.setStatus(HTTPResponseStatus.S200);
		response.setVersion("HTTP/1.1");
		response.setContent("Hybrid Server");

		Reader reader;
		try {
			reader = new InputStreamReader(socket.getInputStream());
			HTTPRequest request = new HTTPRequest(reader);
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

}
