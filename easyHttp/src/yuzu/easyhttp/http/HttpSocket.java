package yuzu.easyhttp.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import yuzu.easyhttp.HttpServer;

public class HttpSocket {
	final Socket socket;
	private OutputStream out;
	private InputStream in;

	final HttpRequest request;
	final HttpResponse response;
	final HttpServer server;

	public HttpSocket(Socket socket, HttpServer server) throws IOException {
		this.socket = socket;
		this.in = this.socket.getInputStream();
		this.server = server;
		this.request = new HttpRequest(this);
		this.response = new HttpResponse(this);
	}

	public void openOutputStream() throws IOException {
		this.out = this.socket.getOutputStream();
	}

	public Socket getSocket() {
		return socket;
	}

	public HttpRequest getHttpRequest() {
		return this.request;
	}

	public HttpResponse getHttpResponse() {
		return this.response;
	}

	public OutputStream getOutput() {
		return out;
	}

	public InputStream getInput() {
		return in;
	}

	public void close() {
		try {
			this.socket.close();
		} catch (IOException e) {}
	}

	public boolean isClosed() {
		return socket.isClosed();
	}

	public HttpServer getServer() {
		return server;
	}
}
