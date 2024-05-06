    public void birdFlap() {
        if (keyIsReleased()) {
            if (isDead())
                return;
            MusicUtil.playFly(); // 播放音效
            state = BIRD_UP;
            if (birdCollisionRect.y > Constant.TOP_BAR_HEIGHT) {
                velocity = ACC_FLAP; // 每次振翅将速度改为上升速度
                wingState = 0; // 重置翅膀状态
            }
            keyPressed();
        }
    }