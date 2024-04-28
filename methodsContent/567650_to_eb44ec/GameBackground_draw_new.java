	public void draw(Graphics g, Bird bird) {
		// 绘制背景色
		g.setColor(Constant.BG_COLOR);
		g.fillRect(0, 0, Constant.FRAME_WIDTH, Constant.FRAME_HEIGHT);

		// 获得背景图片的尺寸
		int imgWidth = BackgroundImg.getWidth();
		int imgHeight = BackgroundImg.getHeight();

		int count = Constant.FRAME_WIDTH / imgWidth + 2; // 根据窗口宽度得到图片的绘制次数
		for (int i = 0; i < count; i++) {
			g.drawImage(BackgroundImg, imgWidth * i - layerX, Constant.FRAME_HEIGHT - imgHeight, null);
		}
		
		if(bird.isDead()) {  //小鸟死亡则不再绘制
			return;
		}
		movement();
	}