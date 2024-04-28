	/**
	 * 从对象池中获取一个对象
	 * 
	 * @return
	 */
	public static Pipe get(String className) {
		if ("Pipe".equals(className)) {
			int size = pool.size();