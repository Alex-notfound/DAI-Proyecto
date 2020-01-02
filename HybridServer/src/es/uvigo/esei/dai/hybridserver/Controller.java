package es.uvigo.esei.dai.hybridserver;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.dao.DAO;
import es.uvigo.esei.dai.hybridserver.dao.DBDAO;
import es.uvigo.esei.dai.hybridserver.entity.Page;

public class Controller {

	DAO dao;

	public Controller(String url, String user, String pass) {
		this.dao = new DBDAO(url, user, pass);
	}

	public Page get(String uuid, String table) throws SQLException {
		return this.dao.get(uuid, table);
	}

	public List<Page> list(String table) throws SQLException {
		return this.dao.list(table);
	}

	public boolean pageFound(String uuid, String table) throws SQLException {
		return this.dao.pageFound(uuid, table);
	}

	public void delete(String uuid, String table) throws SQLException {
		this.dao.delete(new Page(uuid), table);
	}

	public String add(String content, String xsd, String table) throws SQLException {
		String uuid = UUID.randomUUID().toString();
		if (xsd == null) {
			this.dao.create(new Page(uuid, content), table);
		} else {
			this.dao.create(new Page(uuid, content, xsd), table);
		}
		return uuid;
	}

}
