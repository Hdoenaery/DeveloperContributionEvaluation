    public void birdFlap() {
        if (keyIsReleased()) { // ����������ͷ�
            if (state == BIRD_DEAD || state == BIRD_UP || state == BIRD_DEAD_FALL)
                return; // С��������׹��ʱ����
            MusicUtil.playFly(); // ������Ч
            state = BIRD_UP;
            if (birdRect.y > TOP_BOUNDARY) {
                velocity = ACC_FLAP; // ÿ����Ὣ�ٶȸ�Ϊ�����ٶ�
                wingState = 0; // ���ó��״̬
            }
            keyPressed();
        }
    }