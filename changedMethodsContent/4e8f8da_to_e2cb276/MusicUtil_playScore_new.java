	public static void playScore() {
		try {
			// create an audiostream from the inputstream
			scoreIn = new FileInputStream("resources/wav/score.wav");
			score = new AudioStream(scoreIn);
		} catch (FileNotFoundException fnfe) {
		} catch (IOException ioe) {
		}
		AudioPlayer.player.start(score);
	}