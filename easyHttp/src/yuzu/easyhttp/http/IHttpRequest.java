package yuzu.easyhttp.http;

import java.io.InputStream;

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
}
