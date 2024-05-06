	public void draw(Graphics g, Bird bird) {
		// ���Ʊ���ɫ
		g.setColor(Constant.BG_COLOR);
		g.fillRect(0, 0, Constant.FRAME_WIDTH, Constant.FRAME_HEIGHT);

		// ��ñ���ͼƬ�ĳߴ�
		int imgWidth = BackgroundImg.getWidth();
		int imgHeight = BackgroundImg.getHeight();

		int count = Constant.FRAME_WIDTH / imgWidth + 2; // ���ݴ��ڿ�ȵõ�ͼƬ�Ļ��ƴ���
		for (int i = 0; i < count; i++) {
			g.drawImage(BackgroundImg, imgWidth * i - layerX, Constant.FRAME_HEIGHT - imgHeight, null);
		}
		
		if(bird.isDead()) {  //С���������ٻ���
			return;
		}
		movement();
	}