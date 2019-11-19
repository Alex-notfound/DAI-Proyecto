package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.uvigo.esei.dai.hybridserver.entity.Page;

public class XMLDAO implements DAO {

	private String db_url = null;
	private String db_user = null;
	private String db_password = null;

	public XMLDAO(String url, String user, String pass) {
		this.db_url = url;
		this.db_user = user;
		this.db_password = pass;
	}

	@Override
	public void create(Page page) throws SQLException {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {

			String query = "INSERT INTO XML (uuid, content) VALUES (?, ?)";

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, page.getUuid());
				statement.setString(2, page.getContent());

				if (statement.executeUpdate() != 1) {
					throw new RuntimeException("Error en la inserción de página");
				}
			}
		}
	}

	@Override
	public void delete(Page page) throws SQLException {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {
			String query = "DELETE FROM XML WHERE uuid LIKE ?";
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, page.getUuid());

				if (statement.executeUpdate() != 1) {
					throw new RuntimeException("Error en la eliminacion de página");
				}
			}
		}
	}

	@Override
	public Page get(String uuid) throws SQLException {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {

			String query = "SELECT * FROM XML WHERE uuid LIKE ?";

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, uuid);

				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return convertResultIntoPage(result);
					} else {
						System.err.println("Page not found");
						throw new RuntimeException();
					}
				}
			}
		}
	}

	@Override
	public List<Page> list() throws SQLException {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {

			String query = "SELECT * FROM XML";

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
		}
	}

	private Page convertResultIntoPage(ResultSet result) throws SQLException {
		return new Page(result.getString("uuid"), result.getString("content"));
	}

	@Override
	public boolean pageFound(String uuid) throws SQLException {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {

			String query = "SELECT * FROM XML WHERE uuid LIKE ?";

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, uuid);
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean xsdFound(String xsd) {
		// TODO Auto-generated method stub
		return false;
	}

}
