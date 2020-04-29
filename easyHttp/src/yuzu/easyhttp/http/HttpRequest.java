package yuzu.easyhttp.http;

import java.io.IOException;
import java.io.InputStream;

public class HttpRequest extends HttpHead implements IHttpRequest {

	private final HttpSocket hs;
	private final String URL;
	private final String type;
	private String rURL;

	public HttpRequest(HttpSocket hs) throws IOException {
		this.hs = hs;
		InputStream in = this.hs.getInput();
		String info = this.readLine(in);
		String[] infos = info.split(HttpResponse.SPACE);
		//协议get/post
		type = infos[0];
		//地址
		String url = java.net.URLDecoder.decode(infos[1], "utf-8");
		String[] urls = url.split("\\?");
		if (urls.length > 1) {
			URL = urls[0];
		} else URL = url;
		//其他内容
		while (true) {
			info = this.readLine(in);
			if (info.trim().isEmpty()) break;
			infos = info.split(":");
			this.setHeaders(infos[0], infos[1].trim());
		}
	}

	private String readLine(InputStream in) throws IOException {
		String line = "";
		int i;
		while (true) {
			i = in.read();
			if (i == -1) throw new IOException("连接结束！");
			if (i == '\n') break;
			line += (char) i;
		}
		return line;
	}

	@Override
	public String protocol() {
		return type;
	}

	@Override
	public String getURL() {
		return URL;
	}

	@Override
	public String getRelativeURL() {
		return rURL;
	}

	public void setRelativeURL(String mapping) {
		String url = this.getURL();
		int i = mapping.lastIndexOf('/');
		this.rURL = url.substring(i);
	}

	@Override
	public InputStream getInputStream() {
		return hs.getInput();
	}

}
