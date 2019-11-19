package es.uvigo.esei.dai.hybridserver;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.dao.DAO;
import es.uvigo.esei.dai.hybridserver.entity.Page;

public class Controller {

	DAO dao;

	public Controller(DAO dao) {
		this.dao = dao;
	}

	public Page get(String uuid) throws SQLException {
		return this.dao.get(uuid);
	}

	public List<Page> list() throws SQLException {
		return this.dao.list();
	}

	public boolean pageFound(String uuid) throws SQLException {
		return this.dao.pageFound(uuid);
	}

	public void delete(String uuid) throws SQLException {
		this.dao.delete(new Page(uuid));
	}

	public String add(String content) throws SQLException {
		String uuid = UUID.randomUUID().toString();
		this.dao.create(new Page(uuid, content));
		return uuid;
	}
}
