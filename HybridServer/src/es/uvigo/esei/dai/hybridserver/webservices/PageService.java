package es.uvigo.esei.dai.hybridserver.webservices;

import java.sql.SQLException;

import javax.jws.WebService;

@WebService
public interface PageService {

	public String[] getUuids(String table) throws SQLException;

	public String getContent(String uuid, String table) throws SQLException;

	public String getUuid(String uuid) throws SQLException;
}
