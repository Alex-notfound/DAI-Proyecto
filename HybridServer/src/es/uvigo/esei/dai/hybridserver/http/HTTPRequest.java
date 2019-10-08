package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HTTPRequest {

	private HTTPRequestMethod method;
	private String resourceChain;
	private String[] resourcePath;
	private String resourceName;
	private Map<String, String> resourceParameters;
	private String httpVersion;
	private Map<String, String> headerParameters;
	private String content;
	private int contentLength;

	public HTTPRequest(BufferedReader reader) throws IOException, HTTPParseException {

		String s = reader.readLine();

		String[] firstLine = s.split(" ");
		method = HTTPRequestMethod.valueOf(firstLine[0]);

		String[] nameAndParameters = firstLine[1].split("?");
		resourceName = nameAndParameters[0].substring(s.indexOf("/"));

		String[] parameters = nameAndParameters[1].split("&");
		String[] keyAndValue;
		for (int i = 0; i < parameters.length; i++) {
			keyAndValue = parameters[i].split("=");
			resourceParameters.put(keyAndValue[0], keyAndValue[1]);
		}

		while (!(s = reader.readLine()).equals("")) {
			String[] values = s.split(": ");
			headerParameters.put(values[0], values[1]);
		}
		
		if (headerParameters.containsKey("Content-Length")) {
			contentLength = Integer.parseInt(headerParameters.get("Content-Length"));
		}
	}

	public HTTPRequestMethod getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getResourceChain() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getResourcePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getResourceName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getResourceParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHttpVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getHeaderParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getContentLength() {
		// TODO Auto-generated method stub
		return -1;
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
