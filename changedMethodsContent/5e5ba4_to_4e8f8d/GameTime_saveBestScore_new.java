	public void saveBestScore(long time) throws Exception {
		File file = new File(Constant.SCORE_FILE_PATH);
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
		dos.writeLong(time);
		dos.close();
	}