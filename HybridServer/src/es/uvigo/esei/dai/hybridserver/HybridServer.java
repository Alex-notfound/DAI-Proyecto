package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.Endpoint;

import es.uvigo.esei.dai.hybridserver.webservices.ImplHybridServerService;

public class HybridServer {

	private int port;
	private boolean stop;
	private Thread serverThread;
	private ExecutorService threadPool;
	private Controller controller;
	private String webServiceURL;
	private Endpoint ep;

	private String urlDB;
	private String userDB;
	private String passDB;

	public HybridServer() {
		this.urlDB = "jdbc:mysql://localhost:3306/hstestdb";
		this.userDB = "hsdb";
		this.passDB = "hsdbpass";

		this.controller = new Controller("jdbc:mysql://localhost:3306/hstestdb", "hsdb", "hsdbpass", null);
		this.port = 8888;
		this.threadPool = Executors.newFixedThreadPool(50);
	}

	public HybridServer(Properties properties) {
		this.urlDB = properties.getProperty("db.url");
		this.userDB = properties.getProperty("db.user");
		this.passDB = properties.getProperty("db.password");

		this.controller = new Controller(properties.getProperty("db.url"), properties.getProperty("db.user"),
				properties.getProperty("db.password"), null);
		this.port = Integer.valueOf(properties.getProperty("port"));
		threadPool = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty("numClients")));
	}

	public HybridServer(Configuration configuration) {
		this.urlDB = configuration.getDbURL();
		this.userDB = configuration.getDbUser();
		this.passDB = configuration.getDbPassword();

		this.controller = new Controller(configuration.getDbURL(), configuration.getDbUser(),
				configuration.getDbPassword(), configuration.getServers());
		this.port = configuration.getHttpPort();
		this.threadPool = Executors.newFixedThreadPool(configuration.getNumClients());
		this.webServiceURL = configuration.getWebServiceURL();
	}

	public int getPort() {
		return port;
	}

	public void start() {
		Controller controller = this.controller;
		if (this.webServiceURL != null) {
			this.ep = Endpoint.publish(this.webServiceURL, new ImplHybridServerService(this.urlDB, this.userDB, this.passDB));
			ep.setExecutor(threadPool);
		}
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
		if (this.webServiceURL != null) {
			this.ep.stop();
		}
	}
}
