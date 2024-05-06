	public void draw(Graphics g) {
		Fly();
		int state_index = state > STATE_FALL ? STATE_FALL : state; // 图片资源索引
		// 小鸟中心点计算
		int halfImgWidth = birdImgs[state_index][0].getWidth() >> 1;
		int halfImgHeight = birdImgs[state_index][0].getHeight() >> 1;
		if (speed > 0)
			image = birdImgs[STATE_UP][0];
		g.drawImage(image, x - halfImgWidth, y - halfImgHeight, null); // x坐标于窗口1/4处，y坐标位窗口中心

		if (state == STATE_DEAD) {
			drawGameover(g);
		} else if (state == STATE_FALL) {
		} else {
			drawTime(g);
		}

		timing.TimeToScore();
		// 绘制矩形
//		g.setColor(Color.black);
//		g.drawRect((int) birdRect.getX(), (int) birdRect.getY(), (int) birdRect.getWidth(), (int) birdRect.getHeight());
	}