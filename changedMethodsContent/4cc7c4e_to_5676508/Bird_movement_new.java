    private void movement() {
        // 翅膀状态，实现小鸟振翅飞行
        wingState++;
        image = birdImages[Math.min(state, BIRD_DEAD_FALL)][wingState / 10 % IMG_COUNT];

        switch (state) {
            case BIRD_FALL:
                // 自由落体
                if (velocity < MAX_VEL_Y)
                    velocity -= ACC_Y;
                y = Math.min((y - velocity), BOTTOM_BOUNDARY);
                birdRect.y = birdRect.y - velocity;
                if (birdRect.y > BOTTOM_BOUNDARY) {
                    MusicUtil.playCrash();
                    die();
                }
                break;

            case BIRD_DEAD_FALL:
                // 自由落体
                if (velocity < MAX_VEL_Y)
                    velocity -= ACC_Y;
                y = Math.min((y - velocity), BOTTOM_BOUNDARY);
                birdRect.y = birdRect.y - velocity;
                if (birdRect.y > BOTTOM_BOUNDARY) {
                    die();
                }
                break;

            case BIRD_DEAD:
                Game.setGameState(Game.STATE_OVER);
                break;

            case BIRD_NORMAL:
            case BIRD_UP:
                break;
        }

    }