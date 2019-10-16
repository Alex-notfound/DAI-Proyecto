package es.uvigo.esei.dai.hybridserver;

import java.util.Map;

public class Controller {

	MemoryDAO memoryDAO;

	public Controller(MemoryDAO memoryDAO) {
		this.memoryDAO = memoryDAO;
	}

	public String get(String uuid) {
		return this.memoryDAO.pages.get(uuid);
	}
	
	public Map<String, String> getAll() {
		return this.memoryDAO.pages;
	}

	public boolean pageFound(String uuid) {
		return this.memoryDAO.pages.containsKey(uuid);
	}
}
