package es.uvigo.esei.dai.hybridserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HybridServer {
	private static final int SERVICE_PORT = 8888;
	private Thread serverThread;
	private boolean stop;

	private Properties properties = new Properties();
	private ExecutorService threadPool = Executors.newFixedThreadPool(1);
	private Controller controller;

//TODO: Preguntar si está bien y si hay que llamar a close()
	public HybridServer() {
		try (FileOutputStream fos = new FileOutputStream("config.conf")) {

			String s = "numClients=50\r\n" + "port=8888\r\n" + "db.url=jdbc:mysql://localhost:3306/hstestdb\r\n"
					+ "db.user=hsdb\r\n" + "db.password=hsdbpass";
			fos.write(s.getBytes());
			properties.load(new FileInputStream("config.conf"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		threadPool = Executors.newFixedThreadPool((int) properties.get("numClients"));

	}

	public HybridServer(Map<String, String> pages) {
		this.controller = new Controller(new MemoryDAO(pages));
//		threadPool = Executors.newFixedThreadPool((int) properties.get("numClients"));
//		threadPool.execute(new ServiceThread(socket, this.controller));
	}

	public HybridServer(Properties properties) {
		this.properties = properties;
		threadPool = Executors.newFixedThreadPool((int) properties.get("numClients"));
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		Controller controller = this.controller;
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					while (true) {
						Socket socket = serverSocket.accept();
						if (stop)
							break;
						// Responder al cliente
						if (controller == null) {
							threadPool.execute(new ServiceThread(socket));
						} else {
							threadPool.execute(new ServiceThread(socket, controller));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		this.stop = false;
		this.serverThread.start();

	}

	public void stop() {
		this.stop = true;

		try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.serverThread = null;

		this.threadPool.shutdownNow();
		try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
