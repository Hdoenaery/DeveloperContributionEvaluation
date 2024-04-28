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
		// 鸟死后水管停止移动
		if (bird.isDead()) {
			return;
		}
		pipeLogic();

		// 绘制碰撞矩形
//		g.setColor(Color.black);
//		g.drawRect((int) pipeRect.getX(), (int) pipeRect.getY(), (int) pipeRect.getWidth(), (int) pipeRect.getHeight());
	}