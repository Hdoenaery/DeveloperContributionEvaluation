    public void run() {
        while (true) {
            repaint(); // 通过调用repaint(),让JVM调用update()
            try {
                Thread.sleep(GAME_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }