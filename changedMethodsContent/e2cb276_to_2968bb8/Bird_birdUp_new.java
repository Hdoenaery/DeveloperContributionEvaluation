    public void birdUp() {
        if (keyIsReleased()) { // 如果按键已释放
            if (state == STATE_DEAD || state == STATE_UP || state == STATE_FALL)
                return; // 小鸟死亡或坠落时返回
            MusicUtil.playFly(); // 播放音效
            state = STATE_UP;
            speed = SPEED_UP; // 每次振翅将速度改为上升速度
            wingState = 0; // 重置翅膀状态
            keyPressed();
        }
    }