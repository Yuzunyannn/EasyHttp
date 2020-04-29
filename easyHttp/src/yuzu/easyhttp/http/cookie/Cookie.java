package yuzu.easyhttp.http.cookie;

import java.util.ArrayList;
import java.util.List;

import yuzu.easyhttp.http.HttpHead;

public class Cookie {

	public static List<Cookie> parser(String cookieStr) {
		List<Cookie> cookies = new ArrayList<>();
		cookieStr = cookieStr.replace("\\s+", "");
		String[] cookieStrs = cookieStr.split(";");
		for (String str : cookieStrs) {
			String[] values = str.split("=");
			Cookie cookie = new Cookie();
			cookie.name = values[0];
			cookie.value = values[1];
			cookies.add(cookie);
		}
		return cookies;
	}

	private String name;
	private String value;

	public Cookie() {

	}

	public Cookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public boolean isEmpty() {
		return name == null || name.isEmpty();
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(name).append("=").append(value).append(";").append(HttpHead.SPACE);
		return s.toString();
	}

}
