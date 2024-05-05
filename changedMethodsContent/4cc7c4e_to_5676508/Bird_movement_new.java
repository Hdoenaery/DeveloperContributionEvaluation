    private void movement() {
        // ���״̬��ʵ��С��������
        wingState++;
        image = birdImages[Math.min(state, BIRD_DEAD_FALL)][wingState / 10 % IMG_COUNT];

        switch (state) {
            case BIRD_FALL:
                // ��������
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
                // ��������
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