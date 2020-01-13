package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.Provider.Service;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import es.uvigo.esei.dai.hybridserver.webservices.PageService;

public class HybridServer {

	private int port;
	private boolean stop;
	private Thread serverThread;
	private ExecutorService threadPool;
	private Controller controller;
	private String webServiceURL;

	public HybridServer() {
		this.controller = new Controller("jdbc:mysql://localhost:3306/hstestdb", "hsdb", "hsdbpass");
		this.port = 8888;
		this.threadPool = Executors.newFixedThreadPool(50);
	}

	// TODO: Este constructor se elimina?
	public HybridServer(Properties properties) {
		this.controller = new Controller(properties.getProperty("db.url"), properties.getProperty("db.user"),
				properties.getProperty("db.password"));
		this.port = Integer.valueOf(properties.getProperty("port"));
		threadPool = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty("numClients")));
	}

	public HybridServer(Configuration configuration) {
		this.controller = new Controller(configuration.getDbURL(), configuration.getDbUser(),
				configuration.getDbPassword());
		this.port = configuration.getHttpPort();
		this.threadPool = Executors.newFixedThreadPool(configuration.getNumClients());
		this.webServiceURL = configuration.getWebServiceURL();
	}

	public int getPort() {
		return port;
	}

	public void start() {
		Controller controller = this.controller;

//		URL url = new URL("http://localhost:9876/calculus?wsdl");
//		QName name = new QName(this.webServiceURL, "HtmlPageService");
//		Service service = Service.create(url, name);
//		PageService cs = service.getPort(PageService.class);

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
