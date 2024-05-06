	public void draw(Graphics g, Bird bird) {
		// ����ˮ������������ɼ�����ƣ����ɼ���黹
		for (int i = 0; i < pipes.size(); i++) {
			Pipe pipe = pipes.get(i);
			if (pipe.isVisible()) {
				pipe.draw(g, bird);
			} else {
				Pipe remove = pipes.remove(i);
				PipePool.giveBack(remove);
				i--;
			}
		}
		// ��ײ���
		isCollideBird(bird);
		pipeBornLogic(bird);
	}