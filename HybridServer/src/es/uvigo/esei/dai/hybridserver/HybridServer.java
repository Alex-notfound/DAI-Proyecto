package es.uvigo.esei.dai.hybridserver;

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

	private ExecutorService threadPool = Executors.newFixedThreadPool(1);
	private Controller controller;

	public HybridServer() {
//		 properties.put("numClients", 50);
//		 properties.put("port", 8888);
		this.controller = new Controller(new DBDAO("jdbc:mysql://localhost:3306/hstestdb", "hsdb", "hsdbpass"));
		this.threadPool = Executors.newFixedThreadPool(50);
	}

	// TODO: Falla al instanciar el pool
	public HybridServer(Map<String, String> pages) {
		this.controller = new Controller(new MemoryDAO(pages));
		// threadPool = Executors.newFixedThreadPool((int)
		// properties.get("numClients"));
		// threadPool.execute(new ServiceThread(socket, this.controller));
	}

	public HybridServer(Properties properties) {
		// JavaDBConnectionConfiguration configuration = new
		// JavaDBConnectionConfiguration(properties.getProperty("db.url"),
		// properties.getProperty("db.password"), null,
		// properties.getProperty("db.url"))
		this.controller = new Controller(new DBDAO(properties.getProperty("db.url"), properties.getProperty("db.user"),
				properties.getProperty("db.password")));
		threadPool = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty("numClients")));
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	// TODO: Probar en navegador si funciona
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
						threadPool.execute(new ServiceThread(socket, controller));
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
			// Esta conexión se hace, simplemente, para "despertar" el hilo
			// servidor
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
