    public void birdUp() {
        if (keyIsReleased()) { // ����������ͷ�
            if (state == STATE_DEAD || state == STATE_UP || state == STATE_FALL)
                return; // С��������׹��ʱ����
            MusicUtil.playFly(); // ������Ч
            state = STATE_UP;
            speed = SPEED_UP; // ÿ����Ὣ�ٶȸ�Ϊ�����ٶ�
            wingState = 0; // ���ó��״̬
            keyPressed();
        }
    }