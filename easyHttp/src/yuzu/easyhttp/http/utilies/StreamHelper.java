package yuzu.easyhttp.http.utilies;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class StreamHelper {

	public static void to(OutputStream os, InputStream is) throws IOException {
		byte[] bytes = new byte[256];
		int len;
		while ((len = is.read(bytes)) != -1)
			os.write(bytes, 0, len);
	}

	public static void close(OutputStream stream) {
		try {
			if (stream == null) return;
			stream.close();
		} catch (IOException e) {}
	}

	public static void close(InputStream stream) {
		try {
			if (stream == null) return;
			stream.close();
		} catch (IOException e) {}
	}

	public static void close(Socket socket) {
		try {
			if (socket == null) return;
			socket.close();
		} catch (IOException e) {}
	}

}
