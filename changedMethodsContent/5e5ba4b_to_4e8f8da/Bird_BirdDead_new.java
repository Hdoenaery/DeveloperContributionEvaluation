		state = STATE_FALL;
		MusicUtil.playCrash(); // 播放音效
		// 结束计时
		timing.endTiming();
	}

	// 小鸟死亡
	public void birdDead() {
		state = STATE_DEAD;