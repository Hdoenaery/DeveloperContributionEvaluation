	public static Pipe get() {
		int size = pool.size();
		if (size > 0) {
			return pool.remove(size - 1); // 移除并返回最后一个
		} else {
			return new Pipe(); // 空对象池，返回一个新对象
		}
	}