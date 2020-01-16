package es.uvigo.esei.dai.hybridserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import es.uvigo.esei.dai.hybridserver.dao.DAO;
import es.uvigo.esei.dai.hybridserver.dao.DBDAO;
import es.uvigo.esei.dai.hybridserver.entity.Page;
import es.uvigo.esei.dai.hybridserver.webservices.HybridServerService;

public class Controller {

	private DAO dao;
	private List<ServerConfiguration> servers;

	public Controller(String url, String user, String pass, List<ServerConfiguration> servers) {
		this.dao = new DBDAO(url, user, pass);
		this.servers = servers;
	}

	public Page get(String uuid, String table) throws SQLException {
		Page toret = this.dao.get(uuid, table);
		if (toret == null && servers != null) {
			for (ServerConfiguration server : servers) {
				HybridServerService ws = connectWebServices(server);
				if (ws != null) {
					toret = ws.getPage(uuid, table);
					if (toret != null) {
						return toret;
					}
				}
			}
		}
		return toret;
	}

	public String list(String table) throws SQLException {
		List<Page> localList = this.dao.list(table);
		if (localList.isEmpty()) {
			return "Hybrid Server";
		}
		String content = "<html><head></head><body><h2>Local Server</h2>";
		for (Page page : localList) {
			content += "<p><a href=\\html?uuid=" + page.getUuid() + ">" + page.getUuid() + "</a></p>";
		}
		if (servers != null) {
			String[] list;
			int j = 1;
			for (ServerConfiguration server : servers) {
				HybridServerService ws = connectWebServices(server);
				if (ws != null) {
					list = ws.getUuids(table);
					if (list != null) {
						content += "<h2>Server " + j + "</h2>";
						for (int i = 0; i < list.length; i++) {
							content += "<p><a href=" + server.getHttpAddress() + "html?uuid=" + list[i] + ">" + list[i]
									+ "</a></p>";
						}
						j++;
					}
				}
			}
		}
		content += "</body></html>";
		return content;
	}

	public boolean pageFound(String uuid, String table) throws SQLException {
		if (this.dao.pageFound(uuid, table)) {
			return true;
		}
		if (servers != null) {
			for (ServerConfiguration server : servers) {
				HybridServerService ws = connectWebServices(server);
				if (ws != null) {
					if (ws.pageFound(uuid, table)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void delete(String uuid, String table) throws SQLException {
		this.dao.delete(new Page(uuid), table);
	}

	public String add(String content, String xsd, String table) throws SQLException {
		String uuid = UUID.randomUUID().toString();
		if (xsd == null) {
			this.dao.create(new Page(uuid, content), table);
		} else {
			this.dao.create(new Page(uuid, content, xsd), table);
		}
		return uuid;
	}

	public Page getXSLT(String uuid) throws SQLException {
		Page toret = this.dao.getXSLT(uuid);
		if (toret == null && servers != null) {
			for (ServerConfiguration server : servers) {
				HybridServerService ws = connectWebServices(server);
				if (ws != null) {
					toret = ws.getXSLT(uuid);
					if (toret != null) {
						return toret;
					}
				}
			}
		}
		return toret;
	}

	private HybridServerService connectWebServices(ServerConfiguration server) {
		try {
			Service service = Service.create(new URL(server.getWsdl()),
					new QName(server.getNamespace(), server.getService()));
			return service.getPort(HybridServerService.class);
		} catch (MalformedURLException | WebServiceException e) {
			return null;
		}
	}
}
