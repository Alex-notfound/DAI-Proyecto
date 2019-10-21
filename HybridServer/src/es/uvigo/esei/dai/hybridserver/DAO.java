package es.uvigo.esei.dai.hybridserver;

import java.util.Map;

public interface DAO {

	public String get(String uuid);

	public void delete(String uuid);

	public String add(String content);

	public Map<String, String> getAll();

	public boolean pageFound(String uuid);

}
