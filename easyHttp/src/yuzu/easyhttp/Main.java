package yuzu.easyhttp;

import java.io.File;

import yuzu.easyhttp.content.ContentFile;
import yuzu.easyhttp.controller.ControllerResources;

public class Main {

	public static void main(String[] arg) throws Exception {
		HttpServer server = new HttpServer(5656);
		String path = "./web/";
		server.register("/home", (req, rep) -> {
			return new ContentFile(new File(path + "home.html"));
		});
		// server.register("/WebTest/clients", new ControllerWebsocket());
		server.register("/js/*", new ControllerResources(path + "js"));
		server.register("/css/*", new ControllerResources(path + "css"));
		server.register("/img/*.png", new ControllerResources(path + "img"));
	}
}
