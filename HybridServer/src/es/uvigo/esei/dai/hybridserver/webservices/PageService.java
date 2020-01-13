package es.uvigo.esei.dai.hybridserver.webservices;

import java.util.List;

import javax.jws.WebService;

@WebService
public interface PageService {

	public List<String> getUuids(String table);

	public String getContent(String uuid, String table);

	public String getUuid();
}
