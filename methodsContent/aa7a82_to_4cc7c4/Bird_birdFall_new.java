    public void birdFall() {
        state = STATE_FALL;
        MusicUtil.playCrash(); // ������Ч
        speed = 0;  // �ٶ���0����ֹС�����������ˮ���ص�
        // �����澲ֹƬ��
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }