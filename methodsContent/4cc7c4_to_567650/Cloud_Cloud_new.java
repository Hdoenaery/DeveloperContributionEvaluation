    public Cloud(BufferedImage img, int x, int y) {
        super();
        this.img = img;
        this.x = x;
        this.y = y;
        this.speed = Constant.GAME_SPEED * 2; //�ƶ���ٶ�
        // �ƶ�ͼƬ���ŵı��� 1.0~2.0
        double scale = 1 + Math.random(); // Math.random()����0.0~1.0�����ֵ
        // �����ƶ�ͼƬ
        scaleImageWidth = (int) (scale * img.getWidth());
        scaleImageHeight = (int) (scale * img.getWidth());
    }