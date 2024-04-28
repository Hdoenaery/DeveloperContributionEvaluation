        g.setColor(Color.white);
        g.setFont(Constant.SCORE_FONT);
        x = (Constant.FRAME_WIDTH - scoreImg.getWidth() / 2 >> 1) + SCORE_LOCATE;// λ�ò���
        y += scoreImg.getHeight() >> 1;
        String str = Long.toString(countScore.getScore());
        x -= GameUtil.getStringWidth(Constant.SCORE_FONT, str) >> 1;
        y += GameUtil.getStringHeight(Constant.SCORE_FONT, str);
        g.drawString(str, x, y);

        // ������߷���
        if (countScore.getBestScore() > 0) {
            str = Long.toString(countScore.getBestScore());
            x = (Constant.FRAME_WIDTH + scoreImg.getWidth() / 2 >> 1) - SCORE_LOCATE;// λ�ò���
            x -= GameUtil.getStringWidth(Constant.SCORE_FONT, str) >> 1;
            g.drawString(str, x, y);
        }

        // ���Ƽ�����Ϸ��ʹͼ����˸
        final int COUNT = 30; // ��˸����
        if (flash++ > COUNT)
            drawTitle(againImg, g);
        if (flash == COUNT * 2) // ������˸����
            flash = 0;
    }

    // ����С��
    public void reset() {
        state = STATE_NORMAL; // С��״̬
        y = Constant.FRAME_HEIGHT >> 1; // С������
        speed = 0; // С���ٶ�

        int ImgHeight = birdImages[state][0].getHeight();
        birdRect.y = y - ImgHeight / 2 + RECT_DESCALE * 2; // С����ײ��������

        countScore.reset(); // ���üƷ���
        flash = 0;
    }
