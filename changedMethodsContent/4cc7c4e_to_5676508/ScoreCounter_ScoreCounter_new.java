	private ScoreCounter() {
		bestScore = -1;
		try {
			loadBestScore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}