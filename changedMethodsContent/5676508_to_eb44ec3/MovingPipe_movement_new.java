    private void movement() {
        //x������˶��߼�����ͨˮ����ͬ
        x -= speed;
        pipeRect.x -= speed;
        if (x < -1 * PIPE_HEAD_WIDTH) {// ˮ����ȫ�뿪�˴���
            visible = false;
        }

        //ˮ�������ƶ����߼�
        if (direction == DIR_DOWN) {
            dealtY++;
            if (dealtY > MAX_DELTA) {
                direction = DIR_UP;
            }
        } else {
            dealtY--;
            if (dealtY <= 0) {
                direction = DIR_DOWN;
            }
        }
        pipeRect.y = this.y + dealtY;
    }