package yuzu.easyhttp.http;

import java.io.InputStream;

import yuzu.easyhttp.http.cookie.Cookie;
import yuzu.easyhttp.http.session.Session;

public interface IHttpRequest {

	/** 获取全部Url */
	String getURL();

	/** 获取mapping截取后的url */
	String getRelativeURL();

	/** 获取HTTP头内容 */
	String getHeaders(String key);

	/** 获取类型 */
	String protocol();

	/** 获取输入流 */
	InputStream getInputStream();

	/** 获取所有cookie */
	Cookie[] getCookie();

	/** 获取cookie */
	Cookie getCookie(String name);

	/** 获取传递过来的参数 */
	String getParameter(String key);

	/** 获取会话 */
	Session getSession();

	/** 获取socket */
	HttpSocket getSocket();

}
