    private void fly() {
        // ���״̬��ʵ��С��������
        wingState++;
        image = birdImages[Math.min(state, STATE_FALL)][wingState / 10 % IMG_COUNT];

        // �·��߽�: ���ڵĸ߶� - ����ĸ߶� - С��ͼƬ�ĸ߶�
        final int bottomBoundary = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[0][0].getHeight() >> 1);
        final int topBoundary = -50;

        switch (state) {
            case STATE_DOWN:
                // ��������
                speed -= g * T;
                double h = speed * T - g * T * T / 2;
                y = Math.min((int) (y - h), bottomBoundary);
                birdRect.y = Math.min((int) (birdRect.y - h), bottomBoundary);
                if (birdRect.y == bottomBoundary) {
                    MusicUtil.playCrash();
                    birdDead();
                }
                break;

            case STATE_FALL:
                // ��������
                speed -= g * T;
                h = speed * T - g * T * T / 2;
                y = Math.min((int) (y - h), bottomBoundary);
                birdRect.y = Math.min((int) (birdRect.y - h), bottomBoundary);
                if (birdRect.y == bottomBoundary)
                    birdDead();
                break;

            case STATE_DEAD:
                GameFrame.setGameState(GameFrame.STATE_OVER);
                break;

            case STATE_NORMAL:
            case STATE_UP:
                break;
        }

        // �����Ϸ��߽�
        if (birdRect.y < topBoundary) {
            birdRect.y = topBoundary;
            y = topBoundary;
        }

    }