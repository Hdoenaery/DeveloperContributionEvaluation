    private void drawTopHard(Graphics g) {
        // ƴ�ӵĸ���
        int count = (height - PIPE_HEAD_HEIGHT) / PIPE_HEIGHT + 1; // ȡ��+1
        // ����ˮ�ܵ�����
        for (int i = 0; i < count; i++) {
            g.drawImage(imgs[0], x, y + dealtY + i * PIPE_HEIGHT, null);
        }
        // ����ˮ�ܵĶ���
        g.drawImage(imgs[1], x - ((PIPE_HEAD_WIDTH - width) >> 1),
                height - Constant.TOP_PIPE_LENGTHENING - PIPE_HEAD_HEIGHT + dealtY, null);
    }