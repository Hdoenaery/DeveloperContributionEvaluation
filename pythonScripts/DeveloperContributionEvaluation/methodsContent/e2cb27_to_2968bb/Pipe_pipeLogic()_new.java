    private void pipeLogic() {
        x -= speed;
        pipeRect.x -= speed;
        if (x < -1 * PIPE_HEAD_WIDTH) {// 水管完全离开了窗口
            visible = false;
        }
    }