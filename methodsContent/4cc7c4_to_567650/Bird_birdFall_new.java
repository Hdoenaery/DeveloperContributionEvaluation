    public void birdFall() {
        if (state == BIRD_DEAD || state == BIRD_DEAD_FALL)
            return; // С��������׹��ʱ����
        state = BIRD_FALL;
    }