package yuzu.easyhttp.http;

import java.io.OutputStream;

public interface IHttpResponse {

	/** 设置HTTP头内容 */
	void setHeaders(String key, String value);

	/** 开发发送数据，该函数只能调用一次 */
	OutputStream beginSend(int code);

	/** 是否结束发送（是否调用过{@link IHttpResponse#beginSend(int)}） */
	boolean over();

	/** 结束，断开连接 */
	void close();

}
