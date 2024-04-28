	public void saveBestScore(long score) throws Exception {
		File file = new File(Constant.SCORE_FILE_PATH);
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
		dos.writeLong(score);
		dos.close();
	}