    public void reset() {
        state = BIRD_NORMAL; // 小鸟状态
        y = Constant.FRAME_HEIGHT >> 1; // 小鸟坐标
        velocity = 0; // 小鸟速度

        int ImgHeight = birdImages[state][0].getHeight();
        birdRect.y = y - ImgHeight / 2 + RECT_DESCALE * 2; // 小鸟碰撞矩形坐标

        counter.reset(); // 重置计分器
    }