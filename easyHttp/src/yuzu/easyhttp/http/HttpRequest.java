package yuzu.easyhttp.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import yuzu.easyhttp.HttpServer;
import yuzu.easyhttp.http.cookie.Cookie;
import yuzu.easyhttp.http.session.Session;
import yuzu.easyhttp.http.session.Sessions;
import yuzu.easyhttp.util.StreamHelper;

public class HttpRequest extends HttpHead implements IHttpRequest {

	public static final String SESSION_NAME = "session";

	private final HttpSocket hs;
	/** 请求类型 */
	private final String type;
	/** 用户访问的地址 */
	private final String URL;
	/** 相对地址 */
	private String rURL;
	/** 会话 */
	private Session session;
	/** 参数 */
	private Map<String, String> parameters = new TreeMap<>();

	public HttpRequest(HttpSocket hs) throws IOException {
		this.hs = hs;
		InputStream in = this.hs.getInput();
		String info = this.readLine(in);
		String[] infos = info.split(HttpResponse.SPACE);
		// 协议get/post
		type = infos[0].toLowerCase();
		// 地址
		String url = java.net.URLDecoder.decode(infos[1], "utf-8");
		String[] urls = url.split("\\?");
		if (urls.length > 1) {
			URL = urls[0];
			dealParameters(urls[1]);
		} else URL = url;
		// 其他内容
		while (true) {
			info = this.readLine(in);
			if (info.trim().isEmpty()) break;
			infos = info.split(":");
			this.setHeaders(infos[0], infos[1].trim());
		}
		// 获取cookie
		String cookies = this.getHeaders("Cookie");
		if (cookies != null && !cookies.isEmpty()) {
			List<Cookie> list = Cookie.parser(cookies);
			for (Cookie c : list) this.setCookie(c);
		}
		// 获取POST参数
		if ("post".equals(type)) {
			byte[] bytes = getContent(in);
			if (bytes != null) {
				String value = new String(bytes, this.getContentCharset());
				value = java.net.URLDecoder.decode(value, "utf-8");
				dealParameters(value);
			}
		}
		// 获取session
		Cookie sessionCookie = this.getCookie(SESSION_NAME);
		if (sessionCookie != null) {
			this.session = hs.getServer().getSessions().getSession(sessionCookie.getValue());
		}
	}

	private void dealParameters(String str) {
		String[] parStrs = this.split$(str);
		for (String parStr : parStrs) {
			int i = parStr.indexOf('=');
			if (i == -1) continue;
			parameters.put(parStr.substring(0, i), parStr.substring(i + 1));
		}
	}

	private String[] split$(String str) {
		List<String> list = new LinkedList<String>();
		while (!str.isEmpty()) {
			int i = this.find$(str);
			if (i == -1) break;
			list.add(str.substring(0, i));
			str = str.substring(i + 1);
		}
		if (!str.isEmpty()) list.add(str);
		String[] result = new String[list.size()];
		return list.toArray(result);
	}

	private int find$(String str) {
		boolean inString = false;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '"') {
				if (inString) {
					if (str.charAt(i - 1) != '\\') inString = false;
				} else inString = true;
			}
			if (inString) continue;
			if (ch == '&') return i;
		}
		return -1;
	}

	// 获取http内容
	private byte[] getContent(InputStream in) throws IOException {
		String lenStr = this.getHeaders("Content-Length");
		if (lenStr == null) return null;
		int len = 0;
		try {
			len = Integer.parseInt(lenStr);
		} catch (Exception e) {
			return null;
		}
		if (len <= 0) return null;
		return StreamHelper.read(in, len);
	}

	// 读一行
	private String readLine(InputStream in) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int i;
		while (true) {
			i = in.read();
			if (i == -1) throw new IOException("连接结束！");
			if (i == '\n') break;
			buffer.put((byte) i);
		}
		return new String(buffer.array(), HEAD_CAHRSET);
	}

	public String getContentCharset() {
		String content = this.getHeaders("Content-Type");
		if (content == null) return HttpServer.CHARSET;
		int i = content.toLowerCase().lastIndexOf("charset");
		if (i == -1) return HttpServer.CHARSET;
		String[] get = content.substring(i).split("=");
		return get.length > 1 ? get[1].trim() : HttpServer.CHARSET;
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

	@Override
	public String getParameter(String key) {
		return parameters.get(key);
	}

	@Override
	public Session getSession() {
		if (session != null) {
			Sessions sessions = hs.getServer().getSessions();
			session.setExpire(System.currentTimeMillis() + sessions.getLife() * 60 * 1000);
			return session;
		}
		Sessions sessions = hs.getServer().getSessions();
		session = sessions.craeteSession(sessions.genId());
		Cookie cookie = new Cookie(SESSION_NAME, session.getId());
		cookie.setPath("/");
		hs.response.setCookie(cookie);
		return this.session;
	}

	@Override
	public HttpSocket getSocket() {
		return hs;
	}

	@Override
	public InetAddress getAddress() {
		return hs.getSocket().getInetAddress();
	}

}
