	public Cloud(BufferedImage img, int dir, int x, int y) {
		super();
		this.img = img;
		this.dir = dir;
		this.x = x;
		this.y = y;
		
		this.speed = 2; //�ƶ���ٶ�
		scale = 1 + Math.random(); // Math.random()����0.0~1.0�����ֵ
		// �����ƶ�ͼƬ
		scaleImageWidth = (int) (scale * img.getWidth());
		scaleImageHeight = (int) (scale * img.getWidth());
	}