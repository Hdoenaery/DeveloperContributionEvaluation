        g.setColor(Color.white);
        g.setFont(Constant.SCORE_FONT);
        x = (Constant.FRAME_WIDTH - scoreImg.getWidth() / 2 >> 1) + SCORE_LOCATE;// 位置补偿
        y += scoreImg.getHeight() >> 1;
        String str = Long.toString(countScore.getScore());
        x -= GameUtil.getStringWidth(Constant.SCORE_FONT, str) >> 1;
        y += GameUtil.getStringHeight(Constant.SCORE_FONT, str);
        g.drawString(str, x, y);

        // 绘制最高分数
        if (countScore.getBestScore() > 0) {
            str = Long.toString(countScore.getBestScore());
            x = (Constant.FRAME_WIDTH + scoreImg.getWidth() / 2 >> 1) - SCORE_LOCATE;// 位置补偿
            x -= GameUtil.getStringWidth(Constant.SCORE_FONT, str) >> 1;
            g.drawString(str, x, y);
        }

        // 绘制继续游戏，使图像闪烁
        final int COUNT = 30; // 闪烁周期
        if (flash++ > COUNT)
            drawTitle(againImg, g);
        if (flash == COUNT * 2) // 重置闪烁参数
            flash = 0;
    }

    // 重置小鸟
    public void reset() {
        state = STATE_NORMAL; // 小鸟状态
        y = Constant.FRAME_HEIGHT >> 1; // 小鸟坐标
        speed = 0; // 小鸟速度

        int ImgHeight = birdImages[state][0].getHeight();
        birdRect.y = y - ImgHeight / 2 + RECT_DESCALE * 2; // 小鸟碰撞矩形坐标

        countScore.reset(); // 重置计分器
        flash = 0;
    }
