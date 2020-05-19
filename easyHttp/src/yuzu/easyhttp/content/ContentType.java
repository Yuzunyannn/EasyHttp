package yuzu.easyhttp.content;

public enum ContentType {
	TEXT("text/plain", true),
	HTML("text/html", true),
	CSS("text/css", true),
	JPEG("image/jpeg", false),
	GIF("image/gif", false),
	PNG("image/png", false),
	JAVASCRIPT("application/javascript", true),
	JSON("application/json", true),
	XML("application/xml", true),
	PDF("application/pdf", false),
	OCTET("application/octet-stream", false);

	public final String name;
	public final boolean text;

	ContentType(String name, boolean text) {
		this.name = name;
		this.text = text;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
