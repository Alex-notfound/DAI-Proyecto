package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import es.uvigo.esei.dai.hybridserver.DBDAO;
import es.uvigo.esei.dai.hybridserver.DAO;
import es.uvigo.esei.dai.hybridserver.jdbc.*;;


public class Launcher {
	public static void main(String[] args) throws SQLException {
		final DAO dao = createPagesJavaDBDAO(false);
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
	private final static DAO createPagesJavaDBDAO(boolean createDatabase) throws SQLException {
		final JavaDBConnectionConfiguration configuration = new JavaDBConnectionConfiguration(
			"hybridserver", "hybridserver", null, "db/pages"
		);
		// La base de datos ya deberÃ­a estar creada
		if (createDatabase)
			createJavaDBDatabase(configuration);
		
		return new DBDAO(ConnectionUtils.getConnection(configuration));
	}
	
	private final static void createJavaDBDatabase(JavaDBConnectionConfiguration configuration)
	throws SQLException {
		configuration.putAttribute("create", "true");
		final Connection connection = ConnectionUtils.getConnection(configuration);
		connection.createStatement().execute("CREATE SCHEMA HYBRIDSERVER");
		connection.createStatement().execute("CREATE TABLE Pages "
			+ "(id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
			+ "uuid VARCHAR(100) NOT NULL, "
			+ "content VARCHAR(1000) NOT NULL, "
		+ ")");
	}
	
	private final static DAO createEmployeesMySQLDBDAO() throws SQLException {
		final Connection connection = ConnectionUtils.getConnection(
			new MySQLConnectionConfiguration(
					"hybridserver", "hybridserver", "localhost", "pages", 3306
			)
		);
		
		return new DBDAO(connection);
	}

	private static void changeLookAndFeelToNimbus() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another
			// look and feel.
		}
	}
	
}
