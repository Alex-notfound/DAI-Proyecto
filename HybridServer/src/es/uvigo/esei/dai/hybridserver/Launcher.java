package es.uvigo.esei.dai.hybridserver;

import java.util.Properties;

public class Launcher {
	public static void main(String[] args) {
		if (args.length == 1) {
			// TODO: Instanciar properties
			Properties properties = null;
			HybridServer server = new HybridServer(properties);
			server.start();
		} else if (args.length == 0) {
			HybridServer server = new HybridServer();
			server.start();

		} else {
			System.err.println(
							"Número de argumentos no válido. " 
							+ "Para ejecutar la aplicacion ejecute la siguinete línea: \n "
							+ "java es.uvigo.esei.dai.hybridserver.Launch ficheroConfiguracion.conf\r\n");
		}
	}
}
