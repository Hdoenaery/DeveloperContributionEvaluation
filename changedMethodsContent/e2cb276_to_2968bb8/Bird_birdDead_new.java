    public void birdDead() {
        state = STATE_DEAD;
        // 加载游戏结束的资源
        if (overImg == null) {
            overImg = GameUtil.loadBufferedImage(Constant.OVER_IMG_PATH);
            scoreImg = GameUtil.loadBufferedImage(Constant.SCORE_IMG_PATH);
            againImg = GameUtil.loadBufferedImage(Constant.AGAIN_IMG_PATH);
        }
        countScore.isSaveScore(); // 判断是否保存纪录
    }