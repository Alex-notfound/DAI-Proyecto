package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.uvigo.esei.dai.hybridserver.entity.Page;

public class DBDAO implements DAO {

	//TODO: Declaracion atributos esta bien?
	private String db_url = null;
	private String db_user = null;
	private String db_password = null;

	public DBDAO(String url, String user, String pass) {
		this.db_url = url;
		this.db_user = user;
		this.db_password = pass;
	}
//TODO: Preguntar sobre override .. Es necesario? 
	@Override
	public void create(Page page) {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {

			String query = "INSERT INTO HTML (uuid, content) VALUES (?, ?)";

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, page.getUuid());
				statement.setString(2, page.getContent());

				if (statement.executeUpdate() != 1) {
					throw new RuntimeException("Error en la inserción de página");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(Page page) {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {
			String query = "DELETE FROM HTML WHERE uuid LIKE ?";
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, page.getUuid());

				if (statement.executeUpdate() != 1) {
					throw new RuntimeException("Error en la eliminacion de página");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public Page get(String uuid) {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {

			String query = "SELECT * FROM HTML WHERE uuid LIKE ?";

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, uuid);

				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return convertResultIntoPage(result);
					} else {
//					throw new PageNotFoundException(uuid);
						throw new RuntimeException();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Page> list() {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {

			String query = "SELECT * FROM HTML";

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				try (ResultSet result = statement.executeQuery()) {
					List<Page> pages = new ArrayList<>();
					while (result.next()) {
						Page page = convertResultIntoPage(result);
						pages.add(page);
					}
					return pages;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private Page convertResultIntoPage(ResultSet result) throws SQLException {
		return new Page(result.getString("uuid"), result.getString("content"));
	}

	@Override
	public boolean pageFound(String uuid) {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {

			String query = "SELECT * FROM HTML WHERE uuid LIKE ?";

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, uuid);
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return true;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return false;
	}

}
