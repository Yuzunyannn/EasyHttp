package yuzu.easyhttp.util;

import java.util.ArrayList;
import java.util.Comparator;

public class ArrayHeap<T> implements Heap<T> {

	final private ArrayList<T> array = new ArrayList<T>();
	private Comparator<T> cmp;

	public ArrayHeap() {

	}

	public ArrayHeap(Comparator<T> cmp) {
		this.cmp = cmp;
	}

	@Override
	public boolean isEmpty() {
		return array.isEmpty();
	}

	@Override
	public void clear() {
		array.clear();
	}

	@Override
	public T top() {
		return array.get(0);
	}

	@Override
	public boolean remove(T obj) {
		if (obj == null) return false;
		for (int i = 0; i < array.size(); i++) {
			T o = array.get(i);
			if (obj.equals(o)) {
				this.remove(i);
				return true;
			}
		}
		return false;
	}

	public void remove(int index) {
		swap(index, array.size() - 1);
		array.remove(array.size() - 1);
		down(index);
	}

	@Override
	public void push(T obj) {
		array.add(obj);
		this.up(array.size() - 1);
	}

	@Override
	public T pop() {
		T tmp = array.get(0);
		swap(0, array.size() - 1);
		array.remove(array.size() - 1);
		down(0);
		return tmp;
	}

	@SuppressWarnings("unchecked")
	private boolean bigger(int x, int y) {
		T a = array.get(x);
		T b = array.get(y);
		if (cmp != null) return cmp.compare(a, b) > 0;
		return ((Comparable<T>) a).compareTo(b) > 0;
	}

	private void up(int index) {
		if (index <= 0 || index >= array.size()) return;
		int i = this.parent(index);
		if (this.bigger(i, index)) {
			this.swap(i, index);
			this.up(i);
		}
	}

	private void down(int index) {
		int size = array.size();
		if (index < 0 || index >= size) return;
		int left = this.leftSon(index);
		int right = this.rightSon(index);
		if (left >= size) return;
		if (right >= size) {
			if (this.bigger(index, left)) this.swap(index, left);
			return;
		}
		int i = left;
		if (this.bigger(left, right)) i = right;
		if (this.bigger(index, i)) {
			this.swap(index, i);
			this.down(i);
		}
	}

	private void swap(int a, int b) {
		T tmp = array.get(a);
		array.set(a, array.get(b));
		array.set(b, tmp);
	}

	private int parent(int index) {
		return (index - 1) >> 1;
	}

	private int leftSon(int index) {
		return (index << 1) + 1;
	}

	private int rightSon(int index) {
		return (index << 1) + 2;
	}

	@Override
	public String toString() {
		return array.toString();
	}

}
