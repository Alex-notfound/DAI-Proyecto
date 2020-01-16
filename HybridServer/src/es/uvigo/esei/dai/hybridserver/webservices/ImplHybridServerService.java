package es.uvigo.esei.dai.hybridserver.webservices;

import java.sql.SQLException;
import java.util.List;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.dao.DAO;
import es.uvigo.esei.dai.hybridserver.dao.DBDAO;
import es.uvigo.esei.dai.hybridserver.entity.Page;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.webservices.HybridServerService", targetNamespace = "http://hybridserver.dai.esei.uvigo.es/", serviceName = "HybridServerService")
public class ImplHybridServerService implements HybridServerService {

	DAO dao;

	public ImplHybridServerService(String url, String user, String pass) {
		this.dao = new DBDAO(url, user, pass);
	}

	@Override
	public String[] getUuids(String table) throws SQLException {
		List<Page> list = this.dao.list(table);
		String[] toret = new String[list.size()];
		int i = 0;
		for (Page page : list) {
			toret[i] = page.getUuid();
			i++;
		}
		return toret;
	}

	@Override
	public String getContent(String uuid, String table) throws SQLException {
		Page p = this.dao.get(uuid, table);
		if (p != null) {
			return p.getContent();
		}
		return null;
	}

	@Override
	public String getUuid(String uuid) throws SQLException {
		return this.dao.getXSLT(uuid).getXsd();
	}

	// TODO: Como llamarlo?
	public void close() throws Throwable {
		this.finalize();
	}

	@Override
	public boolean pageFound(String uuid, String table) throws SQLException {
		return this.dao.pageFound(uuid, table);
	}

	@Override
	public Page getPage(String uuid, String table) throws SQLException {
		return this.dao.get(uuid, table);
	}

	@Override
	public Page getXSLT(String uuid) throws SQLException {
		return this.dao.getXSLT(uuid);
	}

}
