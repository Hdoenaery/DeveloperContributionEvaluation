    public void birdFall() {
        if (state == BIRD_DEAD || state == BIRD_DEAD_FALL)
            return; // 小鸟死亡或坠落时返回
        state = BIRD_FALL;
    }