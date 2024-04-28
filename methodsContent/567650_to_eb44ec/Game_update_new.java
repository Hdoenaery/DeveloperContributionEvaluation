    public void update(Graphics g) {
        Graphics bufG = bufImg.getGraphics(); // ���ͼƬ����
        // ʹ��ͼƬ���ʽ���Ҫ���Ƶ����ݻ��Ƶ�ͼƬ
        background.draw(bufG, bird); // ������
        foreground.draw(bufG, bird); // ǰ����
        if (gameState == GAME_READY) { // ��Ϸδ��ʼ
            welcomeAnimation.draw(bufG);
        } else { // ��Ϸ����
            gameElement.draw(bufG, bird); // ��ϷԪ�ز�
        }
        bird.draw(bufG);
        g.drawImage(bufImg, 0, 0, null); // һ���Խ�ͼƬ���Ƶ���Ļ��
    }