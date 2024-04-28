    public Bird() {
        countScore = GameScore.getInstance(); // 计分器

        // 读取小鸟图片资源
        birdImages = new BufferedImage[STATE_COUNT][IMG_COUNT];
        for (int j = 0; j < STATE_COUNT; j++) {
            for (int i = 0; i < IMG_COUNT; i++) {
                birdImages[j][i] = GameUtil.loadBufferedImage(Constant.BIRDS_IMG_PATH[j][i]);
            }
        }

        // 初始化小鸟的坐标
        x = Constant.FRAME_WIDTH >> 2;
        y = Constant.FRAME_HEIGHT >> 1;

        int ImgWidth = birdImages[state][0].getWidth();
        int ImgHeight = birdImages[state][0].getHeight();

        // 初始化碰撞矩形
        int rectX = x - ImgWidth / 2;
        int rectY = y - ImgHeight / 2;
        birdRect = new Rectangle(rectX + RECT_DESCALE, rectY + RECT_DESCALE * 2, ImgWidth - RECT_DESCALE * 3,
                ImgHeight - RECT_DESCALE * 4); // 碰撞矩形的坐标与小鸟相同
    }