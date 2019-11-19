package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import es.uvigo.esei.dai.hybridserver.dao.HTMLDAO;
import es.uvigo.esei.dai.hybridserver.dao.MemoryDAO;

public class HybridServer {

	private int port;
	private boolean stop;
	private Thread serverThread;
	private ExecutorService threadPool;
	private Controller controller;

	public HybridServer() {
		this.controller = new Controller(new HTMLDAO("jdbc:mysql://localhost:3306/hstestdb", "hsdb", "hsdbpass"));
		this.port = 8888;
		this.threadPool = Executors.newFixedThreadPool(50);
	}

	public HybridServer(Map<String, String> pages) {
		this.controller = new Controller(new MemoryDAO(pages));
		this.port = 8888;
		this.threadPool = Executors.newFixedThreadPool(50);
	}

	public HybridServer(Properties properties) {
		this.controller = new Controller(new HTMLDAO(properties.getProperty("db.url"), properties.getProperty("db.user"),
				properties.getProperty("db.password")));
		this.port = Integer.valueOf(properties.getProperty("port"));
		threadPool = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty("numClients")));
	}

	public int getPort() {
		return port;
	}

	public void start() {
		Controller controller = this.controller;
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(port)) {
					while (true) {
						Socket socket = serverSocket.accept();
						if (stop)
							break;
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

		try (Socket socket = new Socket("localhost", port)) {
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
