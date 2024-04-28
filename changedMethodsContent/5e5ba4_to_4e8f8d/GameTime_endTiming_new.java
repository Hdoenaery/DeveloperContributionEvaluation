	public void endTiming() {
		endTime = System.currentTimeMillis();
		timeState = STATE_OVER;
		// 判断本次得分是否为最高分
		long score = TimeToScore();
		if (bestScore < score)
			bestScore = score;
		try {
			saveBestScore(bestScore);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}