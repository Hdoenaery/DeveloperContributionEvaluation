    public void run() {
        while (true) {
            repaint(); // ͨ������repaint(),��JVM����update()
            try {
                Thread.sleep(GAME_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }