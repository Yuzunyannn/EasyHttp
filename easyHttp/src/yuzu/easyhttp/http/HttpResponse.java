package yuzu.easyhttp.http;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TimeZone;

import yuzu.easyhttp.http.cookie.Cookie;
import yuzu.easyhttp.util.StreamHelper;

public class HttpResponse extends HttpHead implements IHttpResponse {

	private final HttpSocket hs;
	private boolean over = false;

	public HttpResponse(HttpSocket hs) {
		this.hs = hs;
	}

	@Override
	public OutputStream beginSend(int code) {
		if (over) return hs.getOutput();
		over = true;
		try {
			this.sendResponseHeaders(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hs.getOutput();
	}

	private void sendResponseHeaders(int code) throws IOException {
		hs.openOutputStream();
		OutputStream out = hs.getOutput();
		String head = getHttpHeader(code).toString();
		out.write(head.getBytes(HEAD_CAHRSET));
		out.flush();
	}

	public StringBuilder getHttpHeader(int code) {
		StringBuilder s = new StringBuilder();
		s.append("HTTP/1.1").append(SPACE).append(code).append(SPACE).append(stateCode(code));
		s.append(BLANK_LINE);
		// 时间
		SimpleDateFormat GMT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		GMT.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.setHeaders("Date", GMT.format(Calendar.getInstance().getTime()));
		// cookies
		if (!this.cookies.isEmpty()) {
			StringBuilder cookiesBuiler = new StringBuilder();
			for (Cookie cookie : this.cookies.values()) {
				cookiesBuiler.append(cookie.toString());
			}
			this.setHeaders("Set-Cookie", cookiesBuiler.toString());
		}
		// 其他头
		for (Entry<String, String> entry : map.entrySet()) {
			s.append(entry.getKey()).append(":").append(SPACE).append(entry.getValue());
			s.append(BLANK_LINE);
		}
		return s.append(BLANK_LINE);
	}

	@Override
	public boolean over() {
		return over;
	}

	@Override
	public void close() {
		try {
			hs.getOutput().flush();
		} catch (IOException e) {}
		StreamHelper.close(hs.getInput());
		StreamHelper.close(hs.getOutput());
		hs.close();
	}
}
