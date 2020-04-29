package yuzu.easyhttp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpHead {

	public static final String SPACE = " ";
	public static final String BLANK_LINE = "\r\n";

	protected Map<String, String> map = new HashMap<>();

	public void setHeaders(String key, String value) {
		map.put(key, value);
	}

	public String getHeaders(String key) {
		return map.get(key);
	}

	public String stateCode(int code) {
		switch (code) {
		case 100:
			return "Continue";
		case 101:
			return "Switching Protocols";
		case 200:
			return "OK";
		case 201:
			return "Created";
		case 202:
			return "Accepted";
		case 400:
			return "Bad Request";
		case 403:
			return "Forbidden";
		case 404:
			return "Not Found";
		case 500:
			return "Internal Server Error";
		default:
			return "No message";
		}
	}

}
