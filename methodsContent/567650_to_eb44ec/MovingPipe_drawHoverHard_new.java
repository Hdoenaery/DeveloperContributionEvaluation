    private void drawHoverHard(Graphics g) {
        // ƴ�ӵĸ���
        int count = (height - 2 * PIPE_HEAD_HEIGHT) / PIPE_HEIGHT + 1;
        // ����ˮ�ܵ��϶���
        g.drawImage(imgs[2], x - ((PIPE_HEAD_WIDTH - width) >> 1), y + dealtY, null);
        // ����ˮ�ܵ�����
        for (int i = 0; i < count; i++) {
            g.drawImage(imgs[0], x, y + dealtY + i * PIPE_HEIGHT + PIPE_HEAD_HEIGHT, null);
        }
        // ����ˮ�ܵ��µײ�
        int y = this.y + height - PIPE_HEAD_HEIGHT;
        g.drawImage(imgs[1], x - ((PIPE_HEAD_WIDTH - width) >> 1), y + dealtY, null);
    }