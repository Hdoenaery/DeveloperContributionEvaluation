    public void reset() {
        state = BIRD_NORMAL; // С��״̬
        y = Constant.FRAME_HEIGHT >> 1; // С������
        velocity = 0; // С���ٶ�

        int ImgHeight = birdImages[state][0].getHeight();
        birdRect.y = y - ImgHeight / 2 + RECT_DESCALE * 2; // С����ײ��������

        counter.reset(); // ���üƷ���
    }