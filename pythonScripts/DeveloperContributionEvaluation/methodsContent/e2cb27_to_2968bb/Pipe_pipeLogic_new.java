    private void pipeLogic() {
        x -= speed;
        pipeRect.x -= speed;
        if (x < -1 * PIPE_HEAD_WIDTH) {// ˮ����ȫ�뿪�˴���
            visible = false;
        }
    }