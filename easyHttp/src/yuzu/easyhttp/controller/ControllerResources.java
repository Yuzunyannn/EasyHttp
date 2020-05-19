package yuzu.easyhttp.controller;

import java.io.File;

import yuzu.easyhttp.content.ContentFile;
import yuzu.easyhttp.content.ContentText;
import yuzu.easyhttp.content.ContentType;
import yuzu.easyhttp.http.IHttpRequest;
import yuzu.easyhttp.http.IHttpResponse;

public class ControllerResources implements IController {

	final File folder;

	public ControllerResources(File folder) {
		this.folder = folder;
	}

	public ControllerResources(String folder) {
		this.folder = new File(folder);
	}

	@Override
	public Object handle(IHttpRequest request, IHttpResponse response) {
		String url = request.getRelativeURL();
		if (url.isEmpty() || "/".equals(url)) return this.folderPage(folder, request, response);
		if (url.indexOf("..") != -1) return Code404.instance.handle(request, response);
		File file = new File(folder.getPath() + "/" + url);
		if (file.exists() == false) return Code404.instance.handle(request, response);
		if (file.isDirectory()) return this.folderPage(file, request, response);
		return new ContentFile(file);
	}

	protected Object folderPage(File folder, IHttpRequest request, IHttpResponse response) {
		return Code404.instance.handle(request, response);
	}

	public static class Folder extends ControllerResources {

		public Folder(File folder) {
			super(folder);
		}

		public Folder(String path) {
			super(path);
		}

		@Override
		protected Object folderPage(File folder, IHttpRequest request, IHttpResponse response) {
			File[] files = folder.listFiles();
			String url = request.getURL();
			StringBuilder builder = new StringBuilder();
			builder.append("<html>");
			builder.append("<body>");
			if (url.lastIndexOf('/') == url.length() - 1) url = url.substring(0, url.length() - 1);
			if (!"/".equals(request.getRelativeURL())) this.genDom(builder, url, "..");
			for (File file : files) {
				this.genDom(builder, url, file.getName());
			}
			builder.append("</body>");
			builder.append("</html>");
			return new ContentText(builder.toString(), ContentType.HTML);
		}

		private void genDom(StringBuilder builder, String url, String name) {
			builder.append("<a style='display:block' href='").append(url + "/" + name).append("'>");
			builder.append("./" + name).append("</a>");
		}
	}

}
