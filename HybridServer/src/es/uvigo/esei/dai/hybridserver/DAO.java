package es.uvigo.esei.dai.hybridserver;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.entity.Page;

public interface DAO {

	public void create(Page page);

	public void delete(Page page);

	public Page get(String uuid);

	public List<Page> list();

	public boolean pageFound(String uuid);

}
