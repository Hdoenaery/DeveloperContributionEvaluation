	public boolean isCollideBird(Bird bird) {
		// �������������ж�
		if (bird.isDead()) {
			return false;
		}
		
		//����ˮ������
		for (int i = 0; i < pipes.size(); i++) {
			Pipe pipe = pipes.get(i);
			// �ж���ײ�����Ƿ��н���
			if (pipe.getPipeRect().intersects(bird.getBirdRect())) {
				bird.BirdFall();
				return true;
			}
		}
		return false;
	}