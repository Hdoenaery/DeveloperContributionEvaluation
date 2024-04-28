    private void drawHoverHard(Graphics g) {
        // 拼接的个数
        int count = (height - 2 * PIPE_HEAD_HEIGHT) / PIPE_HEIGHT + 1;
        // 绘制水管的上顶部
        g.drawImage(imgs[2], x - ((PIPE_HEAD_WIDTH - width) >> 1), y + dealtY, null);
        // 绘制水管的主体
        for (int i = 0; i < count; i++) {
            g.drawImage(imgs[0], x, y + dealtY + i * PIPE_HEIGHT + PIPE_HEAD_HEIGHT, null);
        }
        // 绘制水管的下底部
        int y = this.y + height - PIPE_HEAD_HEIGHT;
        g.drawImage(imgs[1], x - ((PIPE_HEAD_WIDTH - width) >> 1), y + dealtY, null);
    }