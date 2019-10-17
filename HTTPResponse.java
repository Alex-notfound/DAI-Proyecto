package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HTTPResponse {

	HTTPResponseStatus status;
	String version;
	String content;
	Map<String, String> parameters = new HashMap<String, String>() ;

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

	public void putParameter(String name, String value) {
		this.parameters.put(name, value);
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
		String statusString = stringStatus(this.status.toString());
		writer.write(this.version +" "+ statusString);
		int length;
		if(this.parameters == null && this.content == null){
			writer.write("\r\n\r\n");
			writer.flush();
			writer.close();
		}else {
			if(this.content != null){
				writer.write("\r\nContent-Length: "+this.content.length());
			}
			if(this.parameters != null ){
				
				for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
				    String key = entry.getKey();
				    String value = entry.getValue();
				    writer.write(key+": "+value+"\n");
				    writer.flush();
				}
				if(this.content == null){
					writer.write("\r\n\r\n");
					writer.flush();
					writer.close();
				}else{
					writer.write("\r\n\r\n");
					writer.write(this.content);
					writer.flush();
					writer.close();
				}
			}
		}
		
	
	}
	public String stringStatus (String status){
		switch(status) {
		  case "S200":
			  return "200 OK";
		    
		  case "S201":
			  return "201 Created";
		    
		  default:
		    // code block
		}

		return " ";
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
