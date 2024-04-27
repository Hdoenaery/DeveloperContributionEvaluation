	public void draw(Graphics g) {
		int x = Constant.FRAME_WIDTH - titleImg.getWidth() >> 1;
		int y = Constant.FRAME_HEIGHT / 5 << 1;
		g.drawImage(titleImg, x, y, null);

		final int COUNT = 30;
		if (flash++ > COUNT) {
			x = Constant.FRAME_WIDTH - noticeImg.getWidth() >> 1;
			y = Constant.FRAME_HEIGHT / 5 * 3;
			g.drawImage(noticeImg, x, y, null);
			if (flash == COUNT * 2)
				flash = 0;
		}
	}