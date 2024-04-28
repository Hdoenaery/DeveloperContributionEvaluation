    private void fly() {
        // 翅膀状态，实现小鸟振翅飞行
        wingState++;
        image = birdImages[Math.min(state, STATE_FALL)][wingState / 10 % IMG_COUNT];

        switch (state) {
            case STATE_DOWN:
                // 物理公式
                speed -= g * T;
                // 小鸟y轴的位移量
                double h = speed * T - g * T * T / 2;
                y = (int) (y - h);
                birdRect.y = (int) (birdRect.y - h);
                // 控制坠落的边界，若y坐标 > 窗口的高度 - 地面的高度 - 小鸟图片的高度则死亡
                if (birdRect.y >= Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1)) {
                    y = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1);
                    birdRect.y = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1);
                    birdFall();
                }

                break;

            case STATE_FALL:
                // 鸟死亡，自由落体
                speed -= g * T;
                h = speed * T - g * T * T / 2;
                y = (int) (y - h);
                birdRect.y = (int) (birdRect.y - h);

                // 控制坠落的边界，若y坐标 > 窗口的高度 - 地面的高度 - 小鸟图片的高度则死亡
                if (birdRect.y >= Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1)) {

                    y = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1);
                    birdRect.y = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImages[state][0].getHeight() >> 1);

                    GameFrame.setGameState(GameFrame.STATE_OVER); // 改变游戏状态
                    birdDead();
                }
                break;

            case STATE_NORMAL:
            case STATE_UP:
            case STATE_DEAD:
                break;
        }

        // 控制上方边界
        if (birdRect.y < -1 * Constant.TOP_PIPE_LENGTHENING / 2) {
            birdRect.y = -1 * Constant.TOP_PIPE_LENGTHENING / 2;
            y = -1 * Constant.TOP_PIPE_LENGTHENING / 2;
        }

        // 控制下方边界
        if (birdRect.y > Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (image.getHeight() >> 1)) {
            birdFall();
        }
    }