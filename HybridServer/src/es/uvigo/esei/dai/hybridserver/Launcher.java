package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Launcher {
	public static void main(String[] args) {
		if (args.length > 1) {
			System.err.println(
					"Número de argumentos no válido. " + "Para ejecutar la aplicacion ejecute la siguinete línea: \n "
							+ "java es.uvigo.esei.dai.hybridserver.Launch ficheroConfiguracion.conf\r\n");
			System.exit(1);
		} else {
			HybridServer server = null;
			if (args.length == 1) {
				// TODO: Instanciar properties
				try (FileInputStream fis = new FileInputStream(new File(args[0]))) {
					Properties properties = new Properties();
					properties.load(fis);
					server = new HybridServer(properties);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				server = new HybridServer();
			}
			server.start();
		}
	}
}
