package es.uvigo.esei.dai.hybridserver;

import java.sql.SQLException;
import java.util.List;

import es.uvigo.esei.dai.hybridserver.entity.Page;

public interface DAO {

	public void create(Page page) throws SQLException;

	public void delete(Page page) throws SQLException;

	public Page get(String uuid) throws SQLException;

	public List<Page> list() throws SQLException;

	public boolean pageFound(String uuid) throws SQLException;

}
