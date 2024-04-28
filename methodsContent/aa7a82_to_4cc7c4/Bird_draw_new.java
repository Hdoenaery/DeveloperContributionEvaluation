    public void draw(Graphics g) {
        fly();
        int state_index = Math.min(state, STATE_FALL); // 图片资源索引
        // 小鸟中心点计算
        int halfImgWidth = birdImages[state_index][0].getWidth() >> 1;
        int halfImgHeight = birdImages[state_index][0].getHeight() >> 1;
        if (speed > 0)
            image = birdImages[STATE_UP][0];
        g.drawImage(image, x - halfImgWidth, y - halfImgHeight, null); // x坐标于窗口1/4处，y坐标位窗口中心

        if (state == STATE_DEAD)
            drawGameOver(g);
        else if (state != STATE_FALL)
            drawScore(g);
        // 绘制矩形
//      g.setColor(Color.black);
//      g.drawRect((int) birdRect.getX(), (int) birdRect.getY(), (int) birdRect.getWidth(), (int) birdRect.getHeight());
    }