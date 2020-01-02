package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.uvigo.esei.dai.hybridserver.entity.Page;

public class DBDAO implements DAO {

	private String db_url = null;
	private String db_user = null;
	private String db_password = null;

	public DBDAO(String url, String user, String pass) {
		this.db_url = url;
		this.db_user = user;
		this.db_password = pass;
	}

	public void create(Page page, String table) throws SQLException {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {
			String query;
			if (table.equals("XSLT")) {
				query = "INSERT INTO " + table + " (uuid, content, xsd) VALUES (?, ?, ?)";
			} else {
				query = "INSERT INTO " + table + " (uuid, content) VALUES (?, ?)";
			}

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, page.getUuid());
				statement.setString(2, page.getContent());
				if (table.equals("XSLT")) {
					statement.setString(3, page.getXsd());
				}

				if (statement.executeUpdate() != 1) {
					throw new RuntimeException("Error en la inserción de página");
				}
			}
		}
	}

	public void delete(Page page, String table) throws SQLException {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {
			String query = "DELETE FROM " + table + " WHERE uuid LIKE ?";
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, page.getUuid());

				if (statement.executeUpdate() != 1) {
					throw new RuntimeException("Error en la eliminacion de página");
				}
			}
		}
	}

	public Page get(String uuid, String table) throws SQLException {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {

			String query = "SELECT * FROM " + table + " WHERE uuid LIKE ?";

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

	public List<Page> list(String table) throws SQLException {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {

			String query = "SELECT * FROM " + table;

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

	public boolean pageFound(String uuid, String table) throws SQLException {
		try (Connection connection = DriverManager.getConnection(db_url, db_user, db_password)) {

			String query = "SELECT * FROM " + table + " WHERE uuid LIKE ?";

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

}
