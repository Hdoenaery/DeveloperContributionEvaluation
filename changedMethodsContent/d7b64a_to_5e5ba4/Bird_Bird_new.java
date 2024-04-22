	public Bird() {
		timing = GameTime.getInstance(); // 计时器

		// 读取小鸟图片资源
		birdImgs = new BufferedImage[STATE_COUNT][IMG_COUNT];
		for (int j = 0; j < STATE_COUNT; j++) {
			for (int i = 0; i < IMG_COUNT; i++) {
				birdImgs[j][i] = GameUtil.loadBUfferedImage(Constant.BIRDS_IMG_PATH[j][i]);
			}
		}

		// 初始化小鸟的坐标
		x = Constant.FRAME_WIDTH >> 2;
		y = Constant.FRAME_HEIGHT >> 1;

		int ImgWidth = birdImgs[state][0].getWidth();
		int ImgHeight = birdImgs[state][0].getHeight();

		// 初始化碰撞矩形
		int rectX = x - ImgWidth / 2;
		int rectY = y - ImgHeight / 2;
		int rectWidth = ImgWidth;
		int rectHeight = ImgHeight;
		birdRect = new Rectangle(rectX + RECT_DESCALE, rectY + RECT_DESCALE * 2, rectWidth - RECT_DESCALE * 3,
				rectHeight - RECT_DESCALE * 4); // 碰撞矩形的坐标与小鸟相同
	}