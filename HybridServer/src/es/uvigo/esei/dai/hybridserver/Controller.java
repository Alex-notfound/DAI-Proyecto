package es.uvigo.esei.dai.hybridserver;

import java.util.Map;

public class Controller {

	MemoryDAO memoryDAO;

	public Controller(MemoryDAO memoryDAO) {
		this.memoryDAO = memoryDAO;
	}

	public String get(String uuid) {
		return this.memoryDAO.get(uuid);
	}

	public Map<String, String> getAll() {
		return this.memoryDAO.pages;
	}

	public boolean pageFound(String uuid) {
		return this.memoryDAO.pages.containsKey(uuid);
	}

	public void delete(String uuid) {
		this.memoryDAO.delete(uuid);
	}

	public String add(String content) {
		return this.memoryDAO.add(content);
	}
}
