package es.uvigo.esei.dai.hybridserver;

import java.util.List;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.entity.Page;

public class Controller {

	DAO dao;

	public Controller(DAO dao) {
		this.dao = dao;
	}

	public Page get(String uuid) {
		return this.dao.get(uuid);
	}

	public List<Page> list() {
		return this.dao.list();
	}

	public boolean pageFound(String uuid) {
		return this.dao.pageFound(uuid);
	}

	public void delete(String uuid) {
		this.dao.delete(new Page(uuid));
	}

	public String add(String content) {
		// TODO: Preguntar si es correcto generar UUID aqui
		String uuid = UUID.randomUUID().toString();
		this.dao.create(new Page(uuid, content));
		return uuid;
	}
}
