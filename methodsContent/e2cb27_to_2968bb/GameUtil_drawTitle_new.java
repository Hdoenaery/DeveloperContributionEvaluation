    public static void drawTitle(BufferedImage image, Graphics g) {
        int x = Constant.FRAME_WIDTH - image.getWidth() >> 1;
        int y = Constant.FRAME_HEIGHT / 5 * 3;
        g.drawImage(image, x, y, null);
    }