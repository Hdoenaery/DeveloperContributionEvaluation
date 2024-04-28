    public void draw(Graphics g) {
        fly();
        int state_index = Math.min(state, STATE_FALL); // ͼƬ��Դ����
        // С�����ĵ����
        int halfImgWidth = birdImages[state_index][0].getWidth() >> 1;
        int halfImgHeight = birdImages[state_index][0].getHeight() >> 1;
        if (speed > 0)
            image = birdImages[STATE_UP][0];
        g.drawImage(image, x - halfImgWidth, y - halfImgHeight, null); // x�����ڴ���1/4����y����λ��������

        if (state == STATE_DEAD)
            drawGameOver(g);
        else if (state != STATE_FALL)
            drawScore(g);
        // ���ƾ���
//      g.setColor(Color.black);
//      g.drawRect((int) birdRect.getX(), (int) birdRect.getY(), (int) birdRect.getWidth(), (int) birdRect.getHeight());
    }