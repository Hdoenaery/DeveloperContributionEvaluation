	public static void playFly() {
		try {
			// create an audiostream from the inputstream
			flyIn = new FileInputStream("resources/wav/fly.wav");
			fly = new AudioStream(flyIn);
		} catch (FileNotFoundException fnfe) {
		} catch (IOException ioe) {
		}
		AudioPlayer.player.start(fly);
	}