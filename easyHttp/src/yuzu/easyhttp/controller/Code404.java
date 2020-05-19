package yuzu.easyhttp.controller;

import java.io.IOException;
import java.io.OutputStream;

import yuzu.easyhttp.http.IHttpRequest;
import yuzu.easyhttp.http.IHttpResponse;

public class Code404 extends Code {

	static final public Code404 instance = new Code404();

	@Override
	protected void handle(OutputStream out, IHttpRequest request, IHttpResponse response) throws IOException {
		out.write("404".getBytes());
	}

	@Override
	protected int code() {
		return 404;
	}

}
