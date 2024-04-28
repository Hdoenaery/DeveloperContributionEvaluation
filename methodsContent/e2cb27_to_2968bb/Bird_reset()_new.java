    public void reset() {
        state = STATE_NORMAL; // 小鸟状态
        y = Constant.FRAME_HEIGHT >> 1; // 小鸟坐标
        speed = 0; // 小鸟速度

        int ImgHeight = birdImages[state][0].getHeight();
        birdRect.y = y - ImgHeight / 2 + RECT_DESCALE * 2; // 小鸟碰撞矩形坐标

        countScore.reset(); // 重置计分器
        flash = 0;
    }