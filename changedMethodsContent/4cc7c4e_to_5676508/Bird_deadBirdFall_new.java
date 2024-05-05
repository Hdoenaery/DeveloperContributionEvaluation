    public void deadBirdFall() {
        state = BIRD_DEAD_FALL;
        MusicUtil.playCrash(); // 播放音效
        velocity = 0;  // 速度置0，防止小鸟继续上升与水管重叠
        // 死后画面静止片刻
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }