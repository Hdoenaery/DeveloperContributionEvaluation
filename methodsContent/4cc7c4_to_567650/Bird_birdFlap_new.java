    public void birdFlap() {
        if (keyIsReleased()) { // 如果按键已释放
            if (state == BIRD_DEAD || state == BIRD_UP || state == BIRD_DEAD_FALL)
                return; // 小鸟死亡或坠落时返回
            MusicUtil.playFly(); // 播放音效
            state = BIRD_UP;
            if (birdRect.y > TOP_BOUNDARY) {
                velocity = ACC_FLAP; // 每次振翅将速度改为上升速度
                wingState = 0; // 重置翅膀状态
            }
            keyPressed();
        }
    }