	public void endTiming() {
		endTime = System.currentTimeMillis();
		timeState = STATE_OVER;
		// �жϱ��ε÷��Ƿ�Ϊ��߷�
		long score = TimeToScore();
		if (bestScore < score)
			bestScore = score;
		try {
			saveBestScore(bestScore);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}