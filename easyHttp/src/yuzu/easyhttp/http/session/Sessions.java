package yuzu.easyhttp.http.session;

import java.util.HashMap;
import java.util.Map;

import yuzu.easyhttp.util.ArrayHeap;
import yuzu.easyhttp.util.Heap;

public class Sessions {

	/** 生命持续时间，分钟 */
	public int life = 30;

	private Map<String, Session> map = new HashMap<>();
	private Heap<SessionTime> heap = new ArrayHeap<>();

	class SessionTime implements Comparable<SessionTime> {
		final long expire;
		final Session session;

		public SessionTime(long expire, Session session) {
			this.expire = expire;
			this.session = session;
		}

		@Override
		public int compareTo(SessionTime o) {
			long interval = expire - o.expire;
			if (interval == 0) return 0;
			return interval > 0 ? 1 : -1;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj instanceof SessionTime) return session == ((SessionTime) obj).session;
			return false;
		}
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = Math.max(life, 1);
	}
	
	/** 获取会话 */
	public Session getSession(String id) {
		if (id == null || id.isEmpty()) return null;
		synchronized (this) {
			return map.get(id);
		}
	}

	/** 创建一个会话 */
	public Session craeteSession(String id) {
		if (id == null || id.isEmpty()) return null;
		synchronized (this) {
			Session session = map.get(id);
			if (session != null) return session;
			session = new Session(id);
			long expire = System.currentTimeMillis() + getLife() * 60 * 1000;
			session.setExpire(expire);
			SessionTime st = new SessionTime(session.getExpire(), session);
			heap.push(st);
			map.put(id, session);
			return session;
		}
	}

	/** 销毁一个会话 */
	public boolean destructSession(String id) {
		synchronized (this) {
			Session session = map.get(id);
			if (session == null) return false;
			map.remove(id);
			heap.remove(new SessionTime(0, session));
			return true;
		}
	}

	/** 检测过期的会话，并删除 */
	public void inspect() {
		if (heap.isEmpty()) return;
		long time = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) synchronized (this) {
			if (this.inspectTop(time)) break;
		}
	}

	private boolean inspectTop(long time) {
		if (heap.isEmpty()) return true;
		SessionTime st = heap.top();
		// 栈顶永远是最小的，如果栈顶到期
		if (st.expire <= time) {
			Session session = st.session;
			if (st.expire >= session.getExpire()) {
				// 如果session到期了
				heap.pop();
				map.remove(session.getId());
			} else {
				// 如果session续命了，那么重新计算规划时间
				heap.pop();
				heap.push(new SessionTime(session.getExpire(), session));
			}
			return false;
		} else return true;
	}

	/** 生成一个id */
	public String genId() {
		while (true) {
			short factor = (short) System.currentTimeMillis();
			factor = (short) Math.abs(factor);
			StringBuilder s = new StringBuilder();
			s.append(factor);
			for (int i = 0; i < factor % 10 + 5; i++) {
				s.append((char) (Math.random() * 24 + 65));
			}
			String id = s.toString();
			if (this.getSession(id) == null) return id;
		}
	}

}
