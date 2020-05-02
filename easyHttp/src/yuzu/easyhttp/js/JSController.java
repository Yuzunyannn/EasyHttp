package yuzu.easyhttp.js;

import javax.script.Invocable;

import yuzu.easyhttp.controller.Code403;
import yuzu.easyhttp.controller.Code500;
import yuzu.easyhttp.controller.IController;
import yuzu.easyhttp.http.IHttpRequest;
import yuzu.easyhttp.http.IHttpResponse;
import yuzu.easyhttp.http.websocket.IWebSocketHandle;
import yuzu.easyhttp.http.websocket.WebSocket;

public class JSController implements IController, IWebSocketHandle {

	final JSDrive driver;
	final Invocable script;
	final Object obj;

	public JSController(JSDrive driver, Object jsObj) {
		this.driver = driver;
		this.script = driver.script;
		this.obj = jsObj;
	}

	@Override
	public Object handle(IHttpRequest request, IHttpResponse response) {
		if ("websocket".equals(request.getHeaders("Upgrade"))) {
			try {
				Boolean accpet = (Boolean) script.invokeMethod(obj, "accpet", request, response);
				if (accpet == Boolean.FALSE) return Code403.instance.handle(request, response);
				WebSocket ws = new WebSocket(request, response, this);
				return ws;
			} catch (NoSuchMethodException e) {
				return Code403.instance.handle(request, response);
			} catch (Exception e) {
				return Code500.instance(e).handle(request, response);
			}
		}

		try {
			return script.invokeMethod(obj, "handle", request, response);
		} catch (Exception e) {
			return Code500.instance(e).handle(request, response);
		}
	}

	@Override
	public void onConnect(WebSocket ws) {
		driver.task(() -> {
			try {
				script.invokeMethod(obj, "onConnect", ws);
			} catch (Exception e) {}
		});
	}

	@Override
	public void onRecv(WebSocket ws, Object msg) {
		driver.task(() -> {
			try {
				script.invokeMethod(obj, "onRecv", ws, msg);
			} catch (Exception e) {}
		});
	}

	@Override
	public void onClose(WebSocket ws) {
		driver.task(() -> {
			try {
				script.invokeMethod(obj, "onClose", ws);
			} catch (Exception e) {}
		});
	}

}
