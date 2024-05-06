	public Cloud(BufferedImage img, int dir, int x, int y) {
		super();
		this.img = img;
		this.dir = dir;
		this.x = x;
		this.y = y;
		
		this.speed = 2; //云朵的速度
		scale = 1 + Math.random(); // Math.random()返回0.0~1.0的随机值
		// 缩放云朵图片
		scaleImageWidth = (int) (scale * img.getWidth());
		scaleImageHeight = (int) (scale * img.getWidth());
	}