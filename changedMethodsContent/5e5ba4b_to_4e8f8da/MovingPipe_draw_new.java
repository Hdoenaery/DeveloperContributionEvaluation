	public void draw(Graphics g, Bird bird) {
		switch (type) {
		case TYPE_HOVER_HARD:
			drawHoverHard(g);
			break;
		case TYPE_TOP_HARD:
			drawTopHard(g);
			break;
		case TYPE_BOTTOM_HARD:
			drawBottomHard(g);
			break;

		}
		// ������ˮ��ֹͣ�ƶ�
		if (bird.isDead()) {
			return;
		}
		pipeLogic();

		// ������ײ����
//		g.setColor(Color.black);
//		g.drawRect((int) pipeRect.getX(), (int) pipeRect.getY(), (int) pipeRect.getWidth(), (int) pipeRect.getHeight());
	}