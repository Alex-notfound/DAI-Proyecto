package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;

public class ServiceThread implements Runnable {

	private Socket socket;

	public ServiceThread(Socket clientSocket) {
		this.socket = clientSocket;
	}

	@Override
	public void run() {
		try (Socket clientSocket = this.socket) {
			// TODO: Dar respuesta
			new HTTPResponse();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
