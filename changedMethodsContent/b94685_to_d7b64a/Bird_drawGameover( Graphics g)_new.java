	private void drawGameover(Graphics g) {
		// 绘制结束标志
		int x = Constant.FRAME_WIDTH - overImg.getWidth() >> 1;
		int y = Constant.FRAME_HEIGHT / 4;
		g.drawImage(overImg, x, y, null);

		// 绘制计分牌
		x = Constant.FRAME_WIDTH - scoreImg.getWidth() >> 1;
		y = Constant.FRAME_HEIGHT / 3;
		g.drawImage(scoreImg, x, y, null);

		// 绘制本局的分数
		g.setColor(Color.white);
		g.setFont(Constant.SCORE_FONT);
		x = (Constant.FRAME_WIDTH - scoreImg.getWidth() / 2 >> 1) + SCORE_LOCATE;// 位置补偿
		y += scoreImg.getHeight() >> 1;
		String str = Long.toString(timing.TimeToScore());
		x -= GameUtil.getStringWidth(Constant.SCORE_FONT, str) >> 1;
		y += GameUtil.getStringHeight(Constant.SCORE_FONT, str);
		g.drawString(str, x, y);

		// 绘制最高分数
		if (timing.getBestScore() > 0) {
			str = Long.toString(timing.getBestScore());
			x = (Constant.FRAME_WIDTH + scoreImg.getWidth() / 2 >> 1) - SCORE_LOCATE;// 位置补偿
			x -= GameUtil.getStringWidth(Constant.SCORE_FONT, str) >> 1;
			g.drawString(str, x, y);
		}
		// 绘制继续游戏
		final int COUNT = 30; // 控制闪烁间隔的参数
		if (flash++ > COUNT) {
			x = Constant.FRAME_WIDTH - againImg.getWidth() >> 1;
			y = Constant.FRAME_HEIGHT / 5 * 3;
			g.drawImage(againImg, x, y, null);
			if (flash == COUNT * 2)
				flash = 0;
		}
	}