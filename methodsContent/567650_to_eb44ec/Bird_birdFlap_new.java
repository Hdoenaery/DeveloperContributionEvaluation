    public void birdFlap() {
        if (keyIsReleased()) {
            if (isDead())
                return;
            MusicUtil.playFly(); // ������Ч
            state = BIRD_UP;
            if (birdCollisionRect.y > Constant.TOP_BAR_HEIGHT) {
                velocity = ACC_FLAP; // ÿ����Ὣ�ٶȸ�Ϊ�����ٶ�
                wingState = 0; // ���ó��״̬
            }
            keyPressed();
        }
    }