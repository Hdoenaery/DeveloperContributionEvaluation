    private void fly() {
        // 翅膀状态，实现小鸟振翅飞行
        wingState++;
        image = birdImages[Math.min(state, STATE_FALL)][wingState / 10 % IMG_COUNT];

        // 下方边界: 窗口的高度 - 地面的高度 - 小鸟图片的高度
        final int bottomBoundary = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[0][0].getHeight() >> 1);
        final int topBoundary = -50;

        switch (state) {
            case STATE_DOWN:
                // 自由落体
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
                // 自由落体
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

        // 控制上方边界
        if (birdRect.y < topBoundary) {
            birdRect.y = topBoundary;
            y = topBoundary;
        }

    }