    private void drawScore(Graphics g) {
        g.setColor(Color.white);
        g.setFont(Constant.CURRENT_SCORE_FONT);
        String str = Long.toString(counter.getCurrentScore());
        int x = Constant.FRAME_WIDTH - GameUtil.getStringWidth(Constant.CURRENT_SCORE_FONT, str) >> 1;
        g.drawString(str, x, Constant.FRAME_HEIGHT / 10);
    }