package es.uvigo.esei.dai.hybridserver;

import java.util.Map;
import java.util.UUID;

public class MemoryDAO implements DAO {

	Map<String, String> pages;

	public MemoryDAO(Map<String, String> pages) {
		this.pages = pages;
	}

	public String get(String uuid) {
		return this.pages.get(uuid);
	}

	public void delete(String uuid) {
		this.pages.remove(uuid);
	}

	public String add(String content) {
		UUID randomUuid = UUID.randomUUID();
		this.pages.put(randomUuid.toString(), content);
		return randomUuid.toString();
	}

	public Map<String, String> getAll() {
		return this.pages;
	}

	public boolean pageFound(String uuid) {
		return this.pages.containsKey(uuid);
	}

}
