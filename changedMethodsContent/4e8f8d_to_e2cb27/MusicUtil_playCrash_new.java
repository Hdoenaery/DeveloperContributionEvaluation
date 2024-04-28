	public static void playCrash() {
		try {
			// create an audiostream from the inputstream
			crashIn = new FileInputStream("resources/wav/crash.wav");
			crash = new AudioStream(crashIn);
		} catch (FileNotFoundException fnfe) {
		} catch (IOException ioe) {
		}
		AudioPlayer.player.start(crash);
	}