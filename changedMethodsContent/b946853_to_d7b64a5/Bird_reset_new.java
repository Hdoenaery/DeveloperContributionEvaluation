	public void reset() {
		state = STATE_NORMAL; // 小鸟状态
		y = Constant.FRAME_HEIGHT >> 1; // 小鸟坐标
		speed = 0; // 小鸟速度

		int ImgHeight = birdImgs[state][0].getHeight();
		birdRect.y = y - ImgHeight / 2 + RECT_DESCALE * 2; // 小鸟碰撞矩形坐标

		timing.reset(); // 计时器
		flash = 0;
	}