    public void birdDead() {
        state = STATE_DEAD;
        // ������Ϸ��������Դ
        if (overImg == null) {
            overImg = GameUtil.loadBufferedImage(Constant.OVER_IMG_PATH);
            scoreImg = GameUtil.loadBufferedImage(Constant.SCORE_IMG_PATH);
            againImg = GameUtil.loadBufferedImage(Constant.AGAIN_IMG_PATH);
        }
        countScore.isSaveScore(); // �ж��Ƿ񱣴��¼
    }