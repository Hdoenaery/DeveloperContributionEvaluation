	private void drawScore(Graphics g) {
		g.setColor(Color.white);
		g.setFont(Constant.TIME_FONT);
		String str = Long.toString(timing.TimeToScore());
		int x = Constant.FRAME_WIDTH - GameUtil.getStringWidth(Constant.TIME_FONT, str) >> 1;
		g.drawString(str, x, Constant.FRAME_HEIGHT / 10);
	}