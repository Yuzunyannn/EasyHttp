package yuzu.easyhttp.controller;

import java.io.File;

import yuzu.easyhttp.content.ContentFile;
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
		if (url.isEmpty()) return Code404.instance.handle(request, response);
		if (url.indexOf("..") != -1) return Code404.instance.handle(request, response);
		File file = new File(folder.getPath() + "/" + url);
		if (file.exists() == false) return Code404.instance.handle(request, response);
		return new ContentFile(file);
	}

}
