package es.uvigo.esei.dai.hybridserver;

import java.util.Map;

public class MemoryDAO implements DAO {

	Map<String, String> pages;

	public MemoryDAO(Map<String, String> pages) {
		this.pages = pages;
	}

	public String get(String uuid) {
		return pages.get(uuid);
	}

}
