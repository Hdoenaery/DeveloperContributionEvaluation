    public Bird() {
        counter = ScoreCounter.getInstance(); // 计分器
        gameOverAnimation = new GameOverAnimation();

        // 读取小鸟图片资源
        birdImages = new BufferedImage[STATE_COUNT][IMG_COUNT];
        for (int j = 0; j < STATE_COUNT; j++) {
            for (int i = 0; i < IMG_COUNT; i++) {
                birdImages[j][i] = GameUtil.loadBufferedImage(Constant.BIRDS_IMG_PATH[j][i]);
            }
        }

        assert birdImages[0][0] != null;
        BIRD_WIDTH = birdImages[0][0].getWidth();
        BIRD_HEIGHT = birdImages[0][0].getHeight();

        // 初始化小鸟的坐标
        x = Constant.FRAME_WIDTH >> 2;
        y = Constant.FRAME_HEIGHT >> 1;

        // 初始化碰撞矩形
        int rectX = x - BIRD_WIDTH / 2;
        int rectY = y - BIRD_HEIGHT / 2;
        birdRect = new Rectangle(rectX + RECT_DESCALE, rectY + RECT_DESCALE * 2, BIRD_WIDTH - RECT_DESCALE * 3,
                BIRD_WIDTH - RECT_DESCALE * 4); // 碰撞矩形的坐标与小鸟相同
    }