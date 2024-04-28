    public static void playCrash() {
        try {
            // create an AudioStream from the InputStream
            InputStream crashIn = new FileInputStream("resources/wav/crash.wav");
            crash = new AudioStream(crashIn);
        } catch (IOException ignored) {
        }
        AudioPlayer.player.start(crash);
    }