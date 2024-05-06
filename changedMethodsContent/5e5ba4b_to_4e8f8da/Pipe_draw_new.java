	public void draw(Graphics g, Bird bird) {
		switch (type) {
		case TYPE_TOP_NORMAL:
			drawTopNormal(g);
			break;
		case TYPE_BOTTOM_NORMAL:
			drawBottomNormal(g);
			break;
		case TYPE_HOVER_NORMAL:
			drawHoverNormal(g);
			break;	
		}
//		//������ײ����
//		g.setColor(Color.black);
//		g.drawRect((int) pipeRect.getX(), (int) pipeRect.getY(), (int) pipeRect.getWidth(), (int) pipeRect.getHeight());
		
		//������ˮ��ֹͣ�ƶ�
		if (bird.isDead()) {
			return;
		}
		pipeLogic();
	}