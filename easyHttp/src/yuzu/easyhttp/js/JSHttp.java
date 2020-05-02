package yuzu.easyhttp.js;

import yuzu.easyhttp.http.cookie.Cookie;

public class JSHttp {

	final JSDrive driver;

	final public JSContent content;

	public JSHttp(JSDrive driver) {
		this.driver = driver;
		this.content = new JSContent(driver);
	}

	public Cookie cookie(String name, String value) {
		return new Cookie(name, value);
	}

	public void log(Object obj) {
		driver.log(obj);
	}
}
