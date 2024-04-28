	public void isSaveScore() {
		long score = getScore();
		if (bestScore < score)
			bestScore = score;
		try {
			saveBestScore(bestScore);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}