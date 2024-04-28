	public void saveScore() {
		bestScore = Math.max(bestScore, getCurrentScore());
		try {
			File file = new File(Constant.SCORE_FILE_PATH);
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
			dos.writeLong(bestScore);
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}