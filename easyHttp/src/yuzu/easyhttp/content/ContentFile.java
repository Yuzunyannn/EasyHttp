package yuzu.easyhttp.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import yuzu.easyhttp.http.IHttpResponse;
import yuzu.easyhttp.http.utilies.StreamHelper;

public class ContentFile extends Content {

	final File file;

	public ContentFile(File file) {
		this.file = file;
		this.checkType();
	}

	public ContentFile(String path) {
		this(new File(path));
	}

	public ContentFile(File file, ContentType type) {
		this.file = file;
		this.type = type;
	}

	public ContentFile(String path, ContentType type) {
		this(new File(path), type);
	}

	public ContentFile(File file, String charset) {
		this.file = file;
		this.charset = charset;
		this.checkType();
	}

	public ContentFile(String path, String charset) {
		this(new File(path), charset);
	}

	public ContentFile(File file, ContentType type, String charset) {
		this.file = file;
		this.type = type;
		this.charset = charset;
	}

	public ContentFile(String path, ContentType type, String charset) {
		this(new File(path), type, charset);
	}

	@Override
	public void write(IHttpResponse response) throws IOException {
		long len = file.length();
		response.setHeaders("Content-Length", Long.toString(len));
		OutputStream out = response.beginSend(200);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			StreamHelper.to(out, in);
			in.close();
		} finally {
			StreamHelper.close(in);
		}
	}

	private void checkType() {
		String name = this.file.getName();
		int dot = name.lastIndexOf('.');
		if (dot == -1) return;
		String suffix = name.substring(dot + 1).toLowerCase();
		switch (suffix) {
		case "html":
			this.type = ContentType.HTML;
			break;
		case "js":
			this.type = ContentType.JAVASCRIPT;
			break;
		case "css":
			this.type = ContentType.CSS;
			break;
		case "json":
			this.type = ContentType.JSON;
			break;
		case "xml":
			this.type = ContentType.XML;
			break;
		case "pdf":
			this.type = ContentType.PDF;
			break;
		case "png":
			this.type = ContentType.PNG;
			break;
		case "jpg":
		case "jpeg":
			this.type = ContentType.JPEG;
			break;
		case "gif":
			this.type = ContentType.GIF;
			break;
		default:
			break;
		}
	}

}
