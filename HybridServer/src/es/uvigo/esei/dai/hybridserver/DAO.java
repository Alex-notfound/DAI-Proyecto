package es.uvigo.esei.dai.hybridserver;

public interface DAO {

	public String get(String uuid);
	public void delete(String uuid);
	public String add(String content); //uuid aleatorio
}
