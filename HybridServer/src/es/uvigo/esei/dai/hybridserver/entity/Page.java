package es.uvigo.esei.dai.hybridserver.entity;

public class Page {

	private String uuid;
	private String content;
	private String xsd;

	public Page() {
	}

	public Page(String uuid) {
		this.uuid = uuid;
	}

	public Page(String uuid, String content) {
		this.uuid = uuid;
		this.content = content;
	}

	public Page(String uuid, String content, String xsd) {
		this.uuid = uuid;
		this.content = content;
		this.xsd = xsd;
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

	public String getXsd() {
		return xsd;
	}

	public void setXsd(String xsd) {
		this.xsd = xsd;
	}

	public String toString() {
		return "Page [uuid=" + uuid + ", content=" + content + "]";
	}
}
