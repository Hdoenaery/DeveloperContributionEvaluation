		state = STATE_FALL;
		MusicUtil.playCrash(); // ������Ч
		// ������ʱ
		timing.endTiming();
	}

	// С������
	public void birdDead() {
		state = STATE_DEAD;