package es.uvigo.esei.dai.hybridserver.entity;

public class Pages {
	private Integer id;
	private String uuid;
	private String content;
	
	public Pages(Integer id,String uuid, String content) {
		this.id = id;
		this.uuid = uuid;
		this.content = content;
	}
	
	
	public Pages(String uuid, String content) {
		this.uuid = uuid;
		this.content = content;
	}
	
	
}
