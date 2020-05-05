package yuzu.easyhttp.http.websocket;

import yuzu.easyhttp.http.IHttpRequest;

public interface IWebSocketHandle {

	/** WS连接时候调用 */
	void onConnect(WebSocket ws, IHttpRequest request);

	/**
	 * 接受数据
	 * 
	 * @param msg
	 *            接受到的消息，可能是String也可能是byte[]
	 * 
	 */
	void onRecv(WebSocket ws, Object msg);

	/** WS结束时候调用 */
	void onClose(WebSocket ws);

}
