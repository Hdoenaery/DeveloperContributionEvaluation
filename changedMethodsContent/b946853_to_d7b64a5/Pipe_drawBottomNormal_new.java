	private void drawBottomNormal(Graphics g) {
		// ƴ�ӵĸ���
		int count = (height - PIPE_HEAD_HEIGHT - Constant.GROUND_HEIGHT) / PIPE_HEIGHT + 1;
		// ����ˮ�ܵ�����
		for (int i = 0; i < count; i++) {
			g.drawImage(imgs[0], x, Constant.FRAME_HEIGHT - PIPE_HEIGHT - Constant.GROUND_HEIGHT - i * PIPE_HEIGHT,
					null);
		}
		// ����ˮ�ܵĶ���
		g.drawImage(imgs[2], x - ((PIPE_HEAD_WIDTH - width) >> 1), Constant.FRAME_HEIGHT - height, null);
	}