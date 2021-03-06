package yuzu.easyhttp.controller;

import java.io.IOException;
import java.io.OutputStream;

import yuzu.easyhttp.http.IHttpRequest;
import yuzu.easyhttp.http.IHttpResponse;

public abstract class Code implements IController {

	@Override
	public Object handle(IHttpRequest request, IHttpResponse response) {
		OutputStream out = response.beginSend(this.code());
		try {
			this.handle(out, request, response);
		} catch (IOException e) {}
		return null;
	}

	protected abstract void handle(OutputStream out, IHttpRequest request, IHttpResponse response) throws IOException;

	protected abstract int code();
}
