package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.SQLException;
import java.util.List;

import es.uvigo.esei.dai.hybridserver.entity.Page;

public interface DAO {

	public void create(Page page, String table) throws SQLException;

	public void delete(Page page, String table) throws SQLException;

	public Page get(String uuid, String table) throws SQLException;

	public List<Page> list(String table) throws SQLException;

	public boolean pageFound(String uuid, String table) throws SQLException;

}
