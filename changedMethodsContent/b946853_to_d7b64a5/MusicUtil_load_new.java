	public static void load() {
		try {
			fly = Applet.newAudioClip(new File("sources/wav/fly.wav").toURL());
			crash = Applet.newAudioClip(new File("sources/wav/crash.wav").toURL());
			score = Applet.newAudioClip(new File("sources/wav/score.wav").toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}