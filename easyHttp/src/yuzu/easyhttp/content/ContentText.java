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
		OutputStream out = response.beginSend(200);
		out.write(text.getBytes(this.getCharset()));
	}

	private void checkType() {
		if (text.matches("\\<.*\\>")) this.type = ContentType.HTML;
		else this.type = ContentType.TEXT;
	}

}
