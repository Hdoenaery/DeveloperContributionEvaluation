    private void fly() {
        // ���״̬��ʵ��С��������
        wingState++;
        image = birdImages[Math.min(state, STATE_FALL)][wingState / 10 % IMG_COUNT];

        switch (state) {
            case STATE_DOWN:
                // ����ʽ
                speed -= g * T;
                // С��y���λ����
                double h = speed * T - g * T * T / 2;
                y = (int) (y - h);
                birdRect.y = (int) (birdRect.y - h);
                // ����׹��ı߽磬��y���� > ���ڵĸ߶� - ����ĸ߶� - С��ͼƬ�ĸ߶�������
                if (birdRect.y >= Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1)) {
                    y = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1);
                    birdRect.y = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1);
                    birdFall();
                }

                break;

            case STATE_FALL:
                // ����������������
                speed -= g * T;
                h = speed * T - g * T * T / 2;
                y = (int) (y - h);
                birdRect.y = (int) (birdRect.y - h);

                // ����׹��ı߽磬��y���� > ���ڵĸ߶� - ����ĸ߶� - С��ͼƬ�ĸ߶�������
                if (birdRect.y >= Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1)) {

                    y = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1);
                    birdRect.y = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1);

                    GameFrame.setGameState(GameFrame.STATE_OVER); // �ı���Ϸ״̬
                    birdDead();
                }
                break;

            case STATE_NORMAL:
            case STATE_UP:
            case STATE_DEAD:
                break;
        }

        // �����Ϸ��߽�
        if (birdRect.y < -1 * Constant.TOP_PIPE_LENGTHENING / 2) {
            birdRect.y = -1 * Constant.TOP_PIPE_LENGTHENING / 2;
            y = -1 * Constant.TOP_PIPE_LENGTHENING / 2;
        }

        // �����·��߽�
        if (birdRect.y > Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (image.getHeight() >> 1)) {
            birdFall();
        }
    }