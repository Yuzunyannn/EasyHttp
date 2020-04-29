package yuzu.easyhttp.http.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class FrameHelper {

	/** websocket的帧信息 */
	public static class WebSocketFrame {
		/** 是否连续 */
		boolean fin;
		/** 操作 */
		int opcode;
		/** 实际数据 */
		byte[] datas;

		void merge(WebSocketFrame b) {
			fin = b.fin;
			byte[] buffer = new byte[datas.length + b.datas.length];
			System.arraycopy(datas, 0, buffer, 0, datas.length);
			System.arraycopy(b.datas, 0, buffer, datas.length, b.datas.length);
		}
	}

	// 读取几个长度的数据
	static protected byte[] read(InputStream input, int len) throws IOException {
		byte[] buffer = new byte[len];
		len = 0;
		while (len < buffer.length) {
			int length = input.read(buffer, len, buffer.length - len);
			if (length == -1) return null;
			len += length;
		}
		return buffer;
	}

	/** 读帧，多帧粘结 */
	static public WebSocketFrame readFrameWithMerge(InputStream input) throws IOException {
		WebSocketFrame frame = null;
		while (true) {
			WebSocketFrame tmpFrame = readFrame(input);
			if (tmpFrame == null) return null;
			if (frame == null) frame = tmpFrame;
			else frame.merge(tmpFrame);
			if (frame.fin) return frame;
		}
	}

	/** 读一个帧 */
	static protected WebSocketFrame readFrame(InputStream input) throws IOException {
		WebSocketFrame frame = new WebSocketFrame();
		int b;
		// 第一个字节
		b = input.read();
		if (b == -1) return null;
		frame.fin = (b & 0x80) != 0 ? true : false;
		frame.opcode = b & 0x0f;
		// 第二个字节
		b = input.read();
		if (b == -1) return null;
		boolean hasMask = (b & 0x80) != 0 ? true : false;
		// 数据长度获取
		int len = b & 0x7f;
		if (len == 126) {
			byte[] buffer = read(input, 2);
			if (buffer == null) return null;
			len = buffer[0] << 8 | buffer[1];
		} else if (len == 127) {
			byte[] buffer = read(input, 8);
			if (buffer == null) return null;
			len = 0;
			for (int i = 0; i < 8; i++) {
				len = (len << 8) | buffer[i];
			}
		}
		// 掩码获取
		byte[] masks = null;
		if (hasMask) {
			masks = read(input, 4);
			if (masks == null) return null;
		}
		// 数据获取
		frame.datas = read(input, len);
		// 掩码计算
		if (hasMask) {
			for (int i = 0; i < len; i++) {
				frame.datas[i] = (byte) (frame.datas[i] ^ masks[i % 4]);
			}
		}
		return frame;
	}

	/** 写入一个帧 */
	static public void writeFrame(OutputStream output, WebSocketFrame frame) throws IOException {
		byte[] buffer;
		if (frame.datas.length < 126) {
			buffer = new byte[2];
			buffer[1] = (byte) frame.datas.length;
		} else if (frame.datas.length < Short.MAX_VALUE) {
			buffer = new byte[4];
			buffer[1] = 126;
			buffer[2] = (byte) ((frame.datas.length >> 8) & 0xff);
			buffer[3] = (byte) ((frame.datas.length >> 0) & 0xff);
		} else {
			buffer = new byte[10];
			buffer[1] = 127;
			for (int i = 0; i < 8; i++) buffer[9 - i] = (byte) ((frame.datas.length >> (i * 8)) & 0xff);
		}
		buffer[0] = (byte) (frame.opcode | (frame.fin ? 0x80 : 0));
		output.write(buffer);
		output.write(frame.datas);
	}

	/** 写一个字符串 */
	static public void writeFarme(OutputStream output, String str) throws IOException {
		WebSocketFrame frame = new WebSocketFrame();
		try {
			frame.datas = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		frame.opcode = 0x01;
		frame.fin = true;
		writeFrame(output, frame);
	}

	/** 写一个二进制流 */
	static public void writeFarme(OutputStream output, byte[] bytes) throws IOException {
		WebSocketFrame frame = new WebSocketFrame();
		frame.opcode = 0x02;
		frame.fin = true;
		frame.datas = bytes;
		writeFrame(output, frame);
	}
}
