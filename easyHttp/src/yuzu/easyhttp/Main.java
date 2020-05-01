package yuzu.easyhttp;

import yuzu.easyhttp.content.ContentFile;
import yuzu.easyhttp.content.ContentText;
import yuzu.easyhttp.content.ContentType;
import yuzu.easyhttp.controller.ControllerResources;
import yuzu.easyhttp.controller.ControllerWebsocket;
import yuzu.easyhttp.http.IHttpRequest;
import yuzu.easyhttp.http.cookie.Cookie;
import yuzu.easyhttp.http.session.Session;
import yuzu.easyhttp.http.websocket.WebSocket;

public class Main {

	public static void main(String[] arg) throws Exception {
		HttpServer server = new HttpServer(5656);
		String path = "./web/";
		// 主页
		server.register("/home", (request, response) -> {
			String test = request.getParameter("test");
			test = test == null ? "123" : test;
			response.setCookie(new Cookie("test", test));
			return new ContentFile(path + "home.html");
		});
		// post测试
		server.register("/info", (request, response) -> {
			String say = request.getParameter("say");
			response.setCookie(new Cookie("say", say));
			Session s = request.getSession();
			if ("whatever".equals(s.getAttribute("usernmae"))) return funny(say);
			else return "请点击测试登陆按钮，登陆后可以看到其他内容！";
		});
		// session测试
		server.register("/login", (request, response) -> {
			Session s = request.getSession();
			s.setAttribute("usernmae", "whatever");
			return new ContentText("{\"code\":1}", ContentType.JSON);
		});
		// webscoket测试
		server.register("/infows", new ControllerWebsocket() {

			@Override
			public void onConnect(WebSocket ws) {

			}

			@Override
			public void onRecv(WebSocket ws, Object msg) {
				ws.send(funny(msg.toString()));
			}

			@Override
			public void onClose(WebSocket ws) {

			}

			@Override
			public boolean accpet(IHttpRequest request) {
				return true;
			}

		});
		// 资源测试
		server.register("/js/*", new ControllerResources(path + "js"));
		server.register("/css/*", new ControllerResources(path + "css"));
		server.register("/img/*.png", new ControllerResources(path + "img"));
	}

	static public String funny(String str) {
		final String[] something = new String[] { "やばいですね～", "你好", "hi", "hello" };
		return something[(int) (Math.random() * something.length)];
	}
}
