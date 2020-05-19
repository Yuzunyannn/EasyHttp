package yuzu.easyhttp.controller;

import java.io.IOException;
import java.io.OutputStream;

import yuzu.easyhttp.http.IHttpRequest;
import yuzu.easyhttp.http.IHttpResponse;

public class Code500 extends Code {

	final Throwable e;

	public static Code500 instance(Throwable e) {
		return new Code500(e);
	}

	public Code500(Throwable e) {
		this.e = e;
	}

	@Override
	protected void handle(OutputStream out, IHttpRequest request, IHttpResponse response) throws IOException {
		out.write(e.toString().getBytes());
	}

	@Override
	protected int code() {
		return 500;
	}
}
