	public void endTiming() {
		endTime = System.currentTimeMillis();
		timeState = STATE_OVER;
		// �жϱ��ε÷��Ƿ�Ϊ��߷�
		long score = TimeToScore();
		if (bestScore < score)
			bestScore = score;
		try {
			saveBestTime(bestScore);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}