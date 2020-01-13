//package es.uvigo.esei.dai.hybridserver.dao;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import es.uvigo.esei.dai.hybridserver.entity.Page;
//
//public class MemoryDAO implements DAO {
//
//	Map<String, String> pages;
//
//	public MemoryDAO(Map<String, String> pages) {
//		this.pages = pages;
//	}
//
//	@Override
//	public void create(Page page) {
//		this.pages.put(page.getUuid(), page.getContent());
//	}
//
//	@Override
//	public void delete(Page page) {
//		this.pages.remove(page.getUuid());
//	}
//
//	@Override
//	public Page get(String uuid) {
//		if (this.pages.containsKey(uuid)) {
//			return new Page(uuid, this.pages.get(uuid));
//		} else {
//			return null;
//		}
//	}
//
//	@Override
//	public List<Page> list() {
//		List<Page> list = new ArrayList<>();
//		for (Map.Entry<String, String> entry : this.pages.entrySet()) {
//			Page page = new Page(entry.getKey(), entry.getValue());
//			list.add(page);
//		}
//		return list;
//	}
//
//	@Override
//	public boolean pageFound(String uuid) {
//		return this.pages.containsKey(uuid);
//	}
//
//	@Override
//	public boolean xsdFound(String xsd) {
//		return false;
//	}
//
//}
