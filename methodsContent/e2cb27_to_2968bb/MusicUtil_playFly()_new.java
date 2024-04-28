    public static void playFly() {
        try {
            // create an AudioStream from the InputStream
            InputStream flyIn = new FileInputStream("resources/wav/fly.wav");
            fly = new AudioStream(flyIn);
        } catch (IOException ignored) {
        }
        AudioPlayer.player.start(fly);
    }