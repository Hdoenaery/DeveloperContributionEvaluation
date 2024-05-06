	// 开始计时的方法
	public void startTiming() {
		if (timing.isReadyTiming())
			timing.startTiming();
	}

	// 绘制实时分数
	private void drawScore(Graphics g) {