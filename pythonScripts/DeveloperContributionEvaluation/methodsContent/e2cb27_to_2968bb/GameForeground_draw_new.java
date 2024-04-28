    public void draw(Graphics g, Bird bird) {
        cloudLogic();
        for (Cloud cloud : clouds) {
            cloud.draw(g, bird);
        }
    }