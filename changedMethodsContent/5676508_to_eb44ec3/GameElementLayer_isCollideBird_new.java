    public void isCollideBird(Bird bird) {
        // �������������ж�
        if (bird.isDead()) {
            return;
        }
        // ����ˮ������
        for (Pipe pipe : pipes) {
            // �ж���ײ�����Ƿ��н���
            if (pipe.getPipeRect().intersects(bird.getBirdCollisionRect())) {
                bird.deadBirdFall();
                return;
            }
        }
    }