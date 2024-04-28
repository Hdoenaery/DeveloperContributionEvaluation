    public static void playScore() {
        try {
            // create an AudioStream from the InputStream
            InputStream scoreIn = new FileInputStream("resources/wav/score.wav");
            score = new AudioStream(scoreIn);
        } catch (IOException ignored) {
        }
        AudioPlayer.player.start(score);
    }