	private void drawGameover(Graphics g) {
		// ���ƽ�����־
		int x = Constant.FRAME_WIDTH - overImg.getWidth() >> 1;
		int y = Constant.FRAME_HEIGHT / 4;
		g.drawImage(overImg, x, y, null);

		// ���ƼƷ���
		x = Constant.FRAME_WIDTH - scoreImg.getWidth() >> 1;
		y = Constant.FRAME_HEIGHT / 3;
		g.drawImage(scoreImg, x, y, null);

		// ���Ʊ��ֵķ���
		g.setColor(Color.white);
		g.setFont(Constant.SCORE_FONT);
		x = (Constant.FRAME_WIDTH - scoreImg.getWidth() / 2 >> 1) + SCORE_LOCATE;// λ�ò���
		y += scoreImg.getHeight() >> 1;
		String str = Long.toString(timing.TimeToScore());
		x -= GameUtil.getStringWidth(Constant.SCORE_FONT, str) >> 1;
		y += GameUtil.getStringHeight(Constant.SCORE_FONT, str);
		g.drawString(str, x, y);

		// ������߷���
		if (timing.getBestScore() > 0) {
			str = Long.toString(timing.getBestScore());
			x = (Constant.FRAME_WIDTH + scoreImg.getWidth() / 2 >> 1) - SCORE_LOCATE;// λ�ò���
			x -= GameUtil.getStringWidth(Constant.SCORE_FONT, str) >> 1;
			g.drawString(str, x, y);
		}
		// ���Ƽ�����Ϸ
		final int COUNT = 30; // ������˸����Ĳ���
		if (flash++ > COUNT) {
			x = Constant.FRAME_WIDTH - againImg.getWidth() >> 1;
			y = Constant.FRAME_HEIGHT / 5 * 3;
			g.drawImage(againImg, x, y, null);
			if (flash == COUNT * 2)
				flash = 0;
		}
	}