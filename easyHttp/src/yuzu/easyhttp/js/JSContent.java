package yuzu.easyhttp.js;

import yuzu.easyhttp.content.ContentFile;
import yuzu.easyhttp.content.ContentText;
import yuzu.easyhttp.content.ContentType;

public class JSContent {

	final JSDrive driver;

	public JSContent(JSDrive driver) {
		this.driver = driver;
	}

	public ContentFile file(String path) {
		return new ContentFile(driver.getPath(path));
	}

	public ContentText text(String text) {
		return new ContentText(text);
	}

	public ContentText json(String text) {
		return new ContentText(text, ContentType.JSON);
	}

}
