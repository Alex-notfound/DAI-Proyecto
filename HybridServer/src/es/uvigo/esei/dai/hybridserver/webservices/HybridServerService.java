package es.uvigo.esei.dai.hybridserver.webservices;

import java.sql.SQLException;

import javax.jws.WebMethod;
import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.entity.Page;

@WebService
public interface HybridServerService {

	@WebMethod
	public String[] getUuids(String table) throws SQLException;

	@WebMethod
	public String getContent(String uuid, String table) throws SQLException;

	@WebMethod
	public Page getPage(String uuid, String table) throws SQLException;

	@WebMethod
	public boolean pageFound(String uuid, String table) throws SQLException;

	@WebMethod
	public String getUuid(String uuid) throws SQLException;

	public Page getXSLT(String uuid) throws SQLException;

}
