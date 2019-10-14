package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HTTPResponse {

	HTTPResponseStatus status;
	String version;
	String content;
	Map<String, String> parameters = new LinkedHashMap<>();

	public HTTPResponse() {
	}

	public HTTPResponseStatus getStatus() {
		return this.status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String putParameter(String name, String value) {
		return this.parameters.put(name, value);
	}

	public boolean containsParameter(String name) {
		return this.parameters.containsKey(name);
	}

	public String removeParameter(String name) {
		return this.parameters.remove(name);
	}

	public void clearParameters() {
		this.parameters.clear();
	}

	public List<String> listParameters() {
		return new ArrayList<String>(this.parameters.values());
	}

	public void print(Writer writer) throws IOException {
		writer.write(this.version + " " + this.status.getCode() + " " + this.status.getStatus());
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			System.out.println(entry.getKey());
			writer.write(entry.getKey() + ": " + entry.getValue() + "\r\n");
		}
		writer.write("\r\n");
		if (content != null) {
			writer.write("Content-Length: " + content.length() + "\r\n\r\n");
			writer.write(this.content);
		} else {
			writer.write("\r\n");
		}
		System.out.println(writer.toString());
		writer.close();
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		return writer.toString();
	}
}
