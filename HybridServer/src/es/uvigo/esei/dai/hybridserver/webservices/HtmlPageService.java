package es.uvigo.esei.dai.hybridserver.webservices;

import java.util.List;

import javax.jws.WebService;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HtmlPageService")
public class HtmlPageService implements PageService {

	@Override
	public List<String> getUuids(String table) {
//		return this.dao.list(table);
		return null;
	}

	@Override
	public String getContent(String uuid, String table) {
//		return this.dao.get(uuid, table);
		return null;
	}

	@Override
	public String getUuid() {
		// TODO Auto-generated method stub
		return null;
	}

}
