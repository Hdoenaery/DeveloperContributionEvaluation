    private void movement() {
        // ³á°ò×´Ì¬£¬ÊµÏÖÐ¡ÄñÕñ³á·ÉÐÐ
        wingState++;
        image = birdImages[Math.min(state, BIRD_DEAD_FALL)][wingState / 10 % IMG_COUNT];
        if (state == BIRD_FALL || state == BIRD_DEAD_FALL) {
            freeFall();
            if (birdCollisionRect.y > BOTTOM_BOUNDARY) {
                if (state == BIRD_FALL) {
                    MusicUtil.playCrash();
                }
                die();
            }
        }
    }