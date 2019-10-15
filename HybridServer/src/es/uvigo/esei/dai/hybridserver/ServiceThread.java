package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

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
