package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.entity.Pages;

public class DBDAO implements DAO {

	
	private Connection connection;

	public DBDAO(Connection connection) {
		this.connection = connection;
	}
	
	
	public String get(String uuid) {
		String query = "SELECT * FROM Pages WHERE uuid="+uuid;
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			try (ResultSet result = statement.executeQuery()) {
				Pages page  = convertResultIntoPage(result);
				return page.getContent();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	public void delete(String uuid){
		String query = "DELETE Pages WHERE uuid="+uuid;
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public String add(String content){
		UUID randomUuid = UUID.randomUUID();
		String query = "INSERT INTO Pages (uuid, content) VALUES (?, ?)";
		try (PreparedStatement statement = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, randomUuid.toString());
			statement.setString(2, content);
			
			if (statement.executeUpdate() != 1) {
				throw new RuntimeException("Error en la inserciÃ³n de la pagina");
			}
			

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return randomUuid.toString();
	}
	
	public HashMap<String,String> listAll(){
		
		String query = "SELECT * FROM pages";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			try (ResultSet result = statement.executeQuery()) {
				HashMap allPages = new HashMap<String,String>();
				while (result.next()) {
					Pages page = convertResultIntoPage(result);
					allPages.put(page.getUuid(), page.getContent());
				}
				return allPages;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public boolean pageFound(String uuid){
		String query = "SELECT * FROM Pages WHERE uuid="+uuid;
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			try (ResultSet result = statement.executeQuery()) {
				if(result==null){
					return false;
				}else return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	private static Pages convertResultIntoPage(ResultSet result) throws SQLException {
		return new Pages(
			result.getInt("id"),
			result.getString("uuid"),
			result.getString("content")
			
		);
	}
}
