    public void deadBirdFall() {
        state = BIRD_DEAD_FALL;
        MusicUtil.playCrash(); // ������Ч
        velocity = 0;  // �ٶ���0����ֹС�����������ˮ���ص�
        // �����澲ֹƬ��
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }