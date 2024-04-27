	public long TimeToScore() {
		long time = getTime();
		long temp = score;
		if (time >= FIRST_SCORE_TIME && time < FIRST_SCORE_TIME + PER_SCORE_TIME) {
			score = 1;
		} else if (time >= FIRST_SCORE_TIME + PER_SCORE_TIME) {
			score = (int) (time - FIRST_SCORE_TIME) / PER_SCORE_TIME + 1;
		}
		if (score - temp > 0) {
			MusicUtil.playScore(); //每次得分播放音效
		}
		return score;
	}