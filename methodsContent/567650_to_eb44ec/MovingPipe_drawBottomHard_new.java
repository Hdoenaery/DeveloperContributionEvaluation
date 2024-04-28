    private void drawBottomHard(Graphics g) {
        // 拼接的个数
        int count = (height - PIPE_HEAD_HEIGHT) / PIPE_HEIGHT + 1;
        // 绘制水管的主体
        for (int i = 0; i < count; i++) {
            g.drawImage(imgs[0], x, Constant.FRAME_HEIGHT - PIPE_HEIGHT - i * PIPE_HEIGHT + dealtY, null);
        }
        // 绘制水管的顶部
        g.drawImage(imgs[2], x - ((PIPE_HEAD_WIDTH - width) >> 1), Constant.FRAME_HEIGHT - height + dealtY, null);
    }