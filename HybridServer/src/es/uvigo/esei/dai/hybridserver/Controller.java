package es.uvigo.esei.dai.hybridserver;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.dao.DAO;
import es.uvigo.esei.dai.hybridserver.dao.HTMLDAO;
import es.uvigo.esei.dai.hybridserver.dao.XMLDAO;
import es.uvigo.esei.dai.hybridserver.dao.XSDDAO;
import es.uvigo.esei.dai.hybridserver.dao.XSLTDAO;
import es.uvigo.esei.dai.hybridserver.entity.Page;

public class Controller {

	DAO dao;
	String url;
	String user;
	String pass;

	public Controller(DAO dao) {
		this.dao = dao;
	}

	public Controller(String url, String user, String pass) {
		this.url = url;
		this.user = user;
		this.pass = pass;
	}

	public void instantiateDao(String type) {
		switch (type) {
		case "html":
			this.dao = new HTMLDAO(url, user, pass);
			break;
		case "xml":
			this.dao = new XMLDAO(url, user, pass);
			break;
		case "xsd":
			this.dao = new XSDDAO(url, user, pass);
			break;
		case "xslt":
			this.dao = new XSLTDAO(url, user, pass);
			break;
		}
	}

	public Page get(String uuid) throws SQLException {
		return this.dao.get(uuid);
	}

	public List<Page> list() throws SQLException {
		return this.dao.list();
	}

	public boolean pageFound(String uuid) throws SQLException {
		return this.dao.pageFound(uuid);
	}

	public void delete(String uuid) throws SQLException {
		this.dao.delete(new Page(uuid));
	}

	public String add(String content, String xsd) throws SQLException {
		String uuid = UUID.randomUUID().toString();
		if (xsd == null) {
			this.dao.create(new Page(uuid, content));
		} else {
			this.dao.create(new Page(uuid, content, xsd));
		}
		return uuid;
	}

	public boolean XsdFound(String xsd) throws SQLException {
		return this.dao.xsdFound(xsd);
	}
}
