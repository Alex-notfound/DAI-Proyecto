package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class DBDAO implements DAO {

	private Connection connection;

	public DBDAO(Connection connection) {
		this.connection = connection;
	}

	public String get(String uuid) {
		String query = "SELECT * FROM Pages WHERE uuid=" + uuid;
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			try (ResultSet result = statement.executeQuery()) {
				Pages page = convertResultIntoPage(result);

			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return null;
	}

	public void delete(String uuid) {

	}

	public String add(String content) {
		return null;
	}

	private static Pages convertResultIntoPage(ResultSet result) throws SQLException {
		return new Pages(result.getInt("id"), result.getString("uuid"), result.getString("content")

		);
	}

	@Override
	public Map<String, String> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean pageFound(String uuid) {
		// TODO Auto-generated method stub
		return false;
	}
}
