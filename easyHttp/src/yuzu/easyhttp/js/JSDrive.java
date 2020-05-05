package yuzu.easyhttp.js;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import yuzu.easyhttp.HttpServer;
import yuzu.easyhttp.content.ContentType;
import yuzu.easyhttp.controller.ControllerResources;
import yuzu.easyhttp.controller.IController;
import yuzu.easyhttp.util.StreamHelper;

public class JSDrive implements Runnable {

	final HttpServer server;
	ScriptEngineManager manager = new ScriptEngineManager();
	ScriptEngine engine = manager.getEngineByName("JavaScript");
	Invocable script = (Invocable) engine;
	String path;
	Thread thread;

	public JSDrive(HttpServer server, String mainJSPath) throws Exception {
		this.server = server;
		File file = new File(mainJSPath);
		path = file.getParent() + "/";
		this.init();
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			engine.eval(reader);
			reader.close();
			try {
				script.invokeFunction("main", this);
			} catch (NoSuchMethodException e) {
				warn("找不到入口main函数");
			}
		} finally {
			StreamHelper.close(reader);
		}
		thread = new Thread(this);
		thread.setName("JSMain");
		thread.start();
	}

	private void init() throws ScriptException {
		engine.put("driver", this);
		JSHttp http = new JSHttp(this);
		engine.put("http", http);
		engine.put("content", http.content);
		engine.put("ContentType", ContentType.class);
	}

	public static final int TICK = 10;

	@Override
	public void run() {
		final int msInterval = 1000 / TICK;
		long nsUpdate;
		while (true) {
			nsUpdate = System.nanoTime();
			this.loop();
			nsUpdate = System.nanoTime() - nsUpdate;
			try {
				long sleepMS = msInterval;
				int sleepNS = (int) nsUpdate;
				if (nsUpdate >= 1000000) {
					int n = (int) (nsUpdate / 1000000);
					sleepMS -= n;
					sleepNS = (int) (nsUpdate - n * 1000000);
				}
				Thread.sleep(sleepMS, sleepNS);
			} catch (Exception e) {}
		}
	}

	Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

	protected void task(Runnable run) {
		tasks.add(run);
	}

	private long tick = 0;
	private ArrayList<Object> listener = new ArrayList<>();

	public void tick(Object obj) {
		listener.add(obj);
	}

	private void loop() {
		tick++;
		while (!tasks.isEmpty()) {
			Runnable task = tasks.poll();
			task.run();
		}
		for (Object obj : listener) {
			try {
				script.invokeMethod(obj, "update", tick);
			} catch (Exception e) {}
		}
	}

	public void load(String js) throws IOException, ScriptException {
		Reader reader = null;
		try {
			reader = new FileReader(new File(this.getPath(js)));
			engine.eval(reader);
			reader.close();
		} finally {
			StreamHelper.close(reader);
		}
	}

	public void warn(Object obj) {
		System.out.println(obj.toString());
	}

	public void log(Object obj) {
		System.out.println(obj.toString());
	}

	public String getPath(String path) {
		if (path.indexOf(":/") != -1 || path.indexOf(":\\") != -1) return path;
		return this.path + path;
	}

	public ControllerResources folder(String path) {
		return new ControllerResources(this.getPath(path));
	}

	public void mapping(String str, Object obj) {
		if (obj instanceof IController) server.register(str, (IController) obj);
		else server.register(str, new JSController(this, obj));
	}
}
