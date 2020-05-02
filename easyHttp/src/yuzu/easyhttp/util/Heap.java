package yuzu.easyhttp.util;

public interface Heap<T> {

	default void add(T obj) {
		this.push(obj);
	}

	boolean isEmpty();

	void clear();

	/** 加入一個元素 */
	void push(T obj);

	/** 彈出堆的顶部 */
	T pop();

	/** 堆的顶部，root */
	T top();

	/** 移除 */
	boolean remove(T obj);

}
