package yuzu.easyhttp.http.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

	protected final String id;
	protected long expire;

	protected Map<String, Object> map = new ConcurrentHashMap<>();

	public Session(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public void setAttribute(String key, Object value) {
		map.put(key, value);
	}

	public void removeAttribute(String key) {
		map.remove(key);
	}

	public Object getAttribute(String key) {
		return map.get(key);
	}

}
