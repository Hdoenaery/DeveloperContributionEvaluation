	public static Pipe get() {
		int size = pool.size();
		if (size > 0) {
			return pool.remove(size - 1); // �Ƴ����������һ��
		} else {
			return new Pipe(); // �ն���أ�����һ���¶���
		}
	}