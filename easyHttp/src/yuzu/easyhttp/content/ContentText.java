package yuzu.easyhttp.content;

import java.io.IOException;
import java.io.OutputStream;

import yuzu.easyhttp.http.IHttpResponse;

public class ContentText extends Content {

	final String text;

	public ContentText(String text) {
		this.text = text;
		this.checkType();
	}

	public ContentText(String text, ContentType type) {
		this.text = text;
		this.type = type;
	}

	public ContentText(String text, String charset) {
		this.text = text;
		this.charset = charset;
		this.checkType();
	}

	public ContentText(String text, ContentType type, String charset) {
		this.text = text;
		this.type = type;
		this.charset = charset;
	}

	@Override
	public void write(IHttpResponse response) throws IOException {
		byte[] bytes = text.getBytes(this.getCharset());
		response.setHeaders("Content-Length", Integer.toString(bytes.length));
		OutputStream out = response.beginSend(code());
		out.write(bytes);
	}

	private void checkType() {
		if (text.matches("\\<.*\\>")) this.type = ContentType.HTML;
		else this.type = ContentType.TEXT;
	}

}
