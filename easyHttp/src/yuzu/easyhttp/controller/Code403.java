package yuzu.easyhttp.controller;

import java.io.IOException;
import java.io.OutputStream;

public class Code403 extends Code {

	static final public Code403 instance = new Code403();

	@Override
	protected void handle(OutputStream out) throws IOException {
	}
	
	@Override
	protected int code() {
		return 403;
	}

}
