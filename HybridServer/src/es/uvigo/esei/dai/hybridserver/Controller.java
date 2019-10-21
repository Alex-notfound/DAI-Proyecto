package es.uvigo.esei.dai.hybridserver;

import java.util.Map;

public class Controller {

	DAO dao;

	public Controller(DAO dao) {
		this.dao = dao;
	}

	public String get(String uuid) {
		return this.dao.get(uuid);
	}

	public Map<String, String> getAll() {
		return this.dao.getAll();
	}

	public boolean pageFound(String uuid) {
		return this.dao.pageFound(uuid);
	}

	public void delete(String uuid) {
		this.dao.delete(uuid);
	}

	public String add(String content) {
		return this.dao.add(content);
	}
}
