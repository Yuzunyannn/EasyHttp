package yuzu.easyhttp.content;

import java.io.File;
import java.io.IOException;

import yuzu.easyhttp.HttpServer;
import yuzu.easyhttp.http.IHttpResponse;

public abstract class Content {

	protected String charset;
	protected ContentType type;

	public String getCharset() {
		return charset == null ? HttpServer.CHARSET : charset;
	}

	public ContentType getType() {
		return type == null ? ContentType.TEXT : type;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setType(ContentType type) {
		this.type = type;
	}

	public int code() {
		return 200;
	}

	abstract public void write(IHttpResponse response) throws IOException;

	public static Content cast(Object obj) {
		if (obj == null) return null;
		if (obj instanceof Content) return (Content) obj;
		if (obj instanceof String) return new ContentText((String) obj);
		else if (obj instanceof Number) return new ContentText(obj.toString(), ContentType.TEXT);
		else if (obj instanceof File) return new ContentFile((File) obj);
		return new ContentText(obj.toString());
	}

}
