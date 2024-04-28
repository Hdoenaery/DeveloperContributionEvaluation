    public Cloud(BufferedImage img, int x, int y) {
        super();
        this.img = img;
        this.x = x;
        this.y = y;
        this.speed = Constant.GAME_SPEED * 2; //云朵的速度
        // 云朵图片缩放的比例 1.0~2.0
        double scale = 1 + Math.random(); // Math.random()返回0.0~1.0的随机值
        // 缩放云朵图片
        scaleImageWidth = (int) (scale * img.getWidth());
        scaleImageHeight = (int) (scale * img.getWidth());
    }