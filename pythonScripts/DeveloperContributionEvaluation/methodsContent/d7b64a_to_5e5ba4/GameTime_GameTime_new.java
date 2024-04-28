	private GameTime() {
		timeState = STATE_READY;
		bestScore = -1;

		try {
			loadBestTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}