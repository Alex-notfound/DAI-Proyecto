package es.uvigo.esei.dai.hybridserver.entity;

public class Page {

	private String uuid;
	private String content;

	public Page() {
	}

	public Page(String uuid) {
		this.uuid = uuid;
	}

	public Page(String uuid, String content) {
		this.uuid = uuid;
		this.content = content;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String toString() {
		return "Page [uuid=" + uuid + ", content=" + content + "]";
	}
}
