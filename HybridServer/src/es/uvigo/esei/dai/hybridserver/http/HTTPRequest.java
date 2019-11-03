package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {

	private HTTPRequestMethod method;
	private String resourceName;
	private Map<String, String> resourceParameters = new LinkedHashMap<>();
	private Map<String, String> headerParameters = new LinkedHashMap<>();
	private String httpVersion;
	private String content;
	private int contentLength = 0;

	private String resourceChain;
	private String[] resourcePath = new String[0];

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		BufferedReader bufferedReader = new BufferedReader(reader);
		String s = bufferedReader.readLine();
		if (s != null) {
			String[] firstLine = s.split(" ");
			if (firstLine.length != 3) {
				throw new HTTPParseException();
			}
			this.method = HTTPRequestMethod.valueOf(firstLine[0]);
			this.resourceChain = firstLine[1];
			this.httpVersion = firstLine[2];

			String[] nameAndParameters = firstLine[1].split("\\?");
			this.resourceName = nameAndParameters[0].substring(1);
			if (!this.resourceName.isEmpty()) {
				this.resourcePath = this.resourceName.split("/");
			}

			if (nameAndParameters.length > 1) {
				String[] parameters = nameAndParameters[1].split("&");
				String[] keyAndValue;
				for (int i = 0; i < parameters.length; i++) {
					keyAndValue = parameters[i].split("=");
					resourceParameters.put(keyAndValue[0], keyAndValue[1]);
				}
			}

			while (!(s = bufferedReader.readLine()).isEmpty() && s != null) {
				String[] values = s.split(": ");
				if (values.length != 2) {
					throw new HTTPParseException();
				}
				headerParameters.put(values[0], values[1]);
			}

			if (headerParameters.containsKey("Content-Length")) {
				contentLength = Integer.parseInt(headerParameters.get("Content-Length"));
			}

			char[] buffer = new char[contentLength];
			bufferedReader.read(buffer);
			for (int i = 0; i < buffer.length; i++) {
				this.content = java.net.URLDecoder.decode(String.valueOf(buffer), "UTF-8");
			}

			if (contentLength > 0) {
				String[] parameters = content.split("&");
				String[] keyAndValue;
				for (int i = 0; i < parameters.length; i++) {
					keyAndValue = parameters[i].split("=");
					resourceParameters.put(keyAndValue[0], keyAndValue[1]);
				}
			}
		}
	}

	public HTTPRequestMethod getMethod() {
		return this.method;
	}

	public String getResourceChain() {
		return this.resourceChain;
	}

	public String[] getResourcePath() {
		return this.resourcePath;
	}

	public String getResourceName() {
		return this.resourceName;
	}

	public Map<String, String> getResourceParameters() {
		return this.resourceParameters;
	}

	public String getHttpVersion() {
		return this.httpVersion;
	}

	public Map<String, String> getHeaderParameters() {
		return this.headerParameters;
	}

	public String getContent() {
		return this.content;
	}

	public int getContentLength() {
		return this.contentLength;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}