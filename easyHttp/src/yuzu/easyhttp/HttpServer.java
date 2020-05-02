package yuzu.easyhttp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

import yuzu.easyhttp.content.Content;
import yuzu.easyhttp.content.ContentType;
import yuzu.easyhttp.controller.Code404;
import yuzu.easyhttp.controller.Code500;
import yuzu.easyhttp.controller.IController;
import yuzu.easyhttp.http.HttpRequest;
import yuzu.easyhttp.http.HttpResponse;
import yuzu.easyhttp.http.HttpSocket;
import yuzu.easyhttp.http.IHttpRequest;
import yuzu.easyhttp.http.IHttpResponse;
import yuzu.easyhttp.http.session.Sessions;
import yuzu.easyhttp.http.websocket.WebSocket;
import yuzu.easyhttp.util.StreamHelper;

public class HttpServer implements Runnable {

	final static public String CHARSET = "utf-8";

	final ServerSocket serverSocket;
	final Thread thread;

	public HttpServer(int port) throws Exception {
		serverSocket = new ServerSocket(port);
		thread = new Thread(this);
		thread.start();
		thread.setName("HttpServer");
	}

	@Override
	public void run() {
		try {
			while (true) {
				Socket socket = serverSocket.accept();
				try {
					HttpSocket hs = new HttpSocket(socket, this);
					this.handle(hs);
				} catch (IOException e) {
					StreamHelper.close(socket);
				} catch (Exception e) {
					e.printStackTrace();
					StreamHelper.close(socket);
				}
				try {
					this.sessions.inspect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void handle(HttpSocket hs) {
		HttpRequest request = hs.getHttpRequest();
		HttpResponse response = hs.getHttpResponse();
		String url = request.getURL();
		if (url.lastIndexOf('/') == url.length() - 1) url = url.substring(0, url.length() - 1);
		for (Map.Entry<String, IController> entry : map.entrySet()) {
			if (url.matches(entry.getKey())) {
				request.setRelativeURL(entry.getKey());
				task(request, response, entry.getValue());
				return;
			}
		}
		task(request, response, Code404.instance);
	}

	private void task(IHttpRequest request, IHttpResponse response, IController controller) {
		Runnable task = () -> {
			try {
				// 长连接
				if ("websocket".equals(request.getHeaders("Upgrade"))) {
					Object ret = controller.handle(request, response);
					if (ret instanceof WebSocket) return;
					response.beginSend(403);
					response.close();
					return;
				}
				// 短连接
				Object ret = controller.handle(request, response);
				if (response.over()) {
					response.close();
					return;
				}
				Content content = Content.cast(ret);
				if (content == null) {
					response.beginSend(200);
					response.close();
					return;
				}
				ContentType type = content.getType();
				if (type.text) response.setHeaders("Content-Type", type.name + ";charset=" + content.getCharset());
				else response.setHeaders("Content-Type", type.name);
				content.write(response);
				response.close();
			} catch (Exception e) {
				new Code500(e).handle(request, response);
				response.close();
			}
		};
		// 这里可以开线程，或者进行线程池任务提交等等
		task.run();
	}

	final Sessions sessions = new Sessions();

	public Sessions getSessions() {
		return sessions;
	}

	private Map<String, IController> map = new LinkedHashMap<>();

	/**
	 * 注册一个控制器
	 * 
	 * @param mapping
	 *            映射，形如"/test"或者"/test1/*"或者"/test2/*.png"
	 * 
	 * @param controller
	 *            控制器处理连接
	 */
	public void register(String mapping, IController controller) {
		if (mapping.lastIndexOf('/') == mapping.length() - 1) mapping = mapping.substring(0, mapping.length() - 1);
		mapping = mapping.replace("*", ".*");
		map.put(mapping, controller);
	}
}
