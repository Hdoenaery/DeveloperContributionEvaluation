    public void draw(Graphics g, Bird bird) {
        cloudBornLogic();
        for (Cloud cloud : clouds) {
            cloud.draw(g, bird);
        }
    }