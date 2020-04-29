package yuzu.easyhttp.controller;

import yuzu.easyhttp.http.IHttpRequest;
import yuzu.easyhttp.http.IHttpResponse;
import yuzu.easyhttp.http.websocket.IWebSocketHandle;
import yuzu.easyhttp.http.websocket.WebSocket;

public abstract class ControllerWebsocket implements IController, IWebSocketHandle {

	@Override
	public Object handle(IHttpRequest request, IHttpResponse response) {
		if (!"websocket".equals(request.getHeaders("Upgrade"))) return Code403.instance.handle(request, response);
		try {
			boolean accpet = this.accpet(request);
			if (accpet == false) return Code403.instance.handle(request, response);;
			WebSocket ws = new WebSocket(request, response, this);
			return ws;
		} catch (Exception e) {
			return Code500.instance(e).handle(request, response);
		}
	}

	protected abstract boolean accpet(IHttpRequest request);

}
