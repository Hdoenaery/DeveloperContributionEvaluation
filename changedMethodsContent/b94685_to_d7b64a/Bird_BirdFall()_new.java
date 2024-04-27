	public void BirdFall() {
		state = STATE_FALL;
		MusicUtil.playCrash(); // 播放音效
		// 结束计时
		timing.endTiming();
	}