package yuzu.easyhttp.http.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import yuzu.easyhttp.http.IHttpRequest;
import yuzu.easyhttp.http.IHttpResponse;
import yuzu.easyhttp.http.websocket.FrameHelper.WebSocketFrame;

public class WebSocket implements Runnable {

	OutputStream out;
	InputStream in;
	IHttpResponse response;
	IHttpRequest request;
	IWebSocketHandle handle;

	Map<String, Object> map = new HashMap<>();

	public WebSocket(IHttpRequest request, IHttpResponse response, IWebSocketHandle handle) throws Exception {
		out = this.webSocketResponse(request, response);
		in = request.getInputStream();
		this.response = response;
		this.request = request;
		this.handle = handle;
		new Thread(this).start();
	}

	// websocket答复
	private OutputStream webSocketResponse(IHttpRequest request, IHttpResponse response) throws Exception {
		response.setHeaders("Upgrade", "websocket");
		response.setHeaders("Connection", "Upgrade");
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		sha1.update(request.getHeaders("Sec-WebSocket-Key").getBytes("utf-8"));
		sha1.update("258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes("utf-8"));
		response.setHeaders("Sec-Websocket-Accept", Base64.getEncoder().encodeToString(sha1.digest()));
		return response.beginSend(101);
	}

	public void close() {
		response.close();
	}

	public boolean send(String str) {
		try {
			FrameHelper.writeFarme(out, str);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public void run() {
		try {
			boolean run = true;
			this.handle.onConnect(this, request);
			while (run) {
				WebSocketFrame frame = FrameHelper.readFrameWithMerge(in);
				if (frame == null) break;
				switch (frame.opcode) {
				case 0:
					throw new Exception("不应当出现延续数据！");
				case 1:
					this.handle.onRecv(this, new String(frame.datas, "utf-8"));
					break;
				case 2:
					this.handle.onRecv(this, frame.datas);
					break;
				case 8:
					run = false;
					break;
				case 9:
					System.out.println("不支持ping操作");
					break;
				case 10:
					System.out.println("不支持pong操作");
					break;
				default:
					System.out.println("不支持的操作:" + frame.opcode);
					break;
				}
			}
		} catch (Throwable e) {
			if (!"Socket closed".equals(e.getMessage())) e.printStackTrace();
		}
		this.response.close();
		this.handle.onClose(this);
	}

	public Object getParameter(String key) {
		return map.get(key);
	}

	public void setParameter(String key, Object value) {
		map.put(key, value);
	}

}
