	public GameForeground() {
		clouds = new ArrayList<>(); //云朵的容器
		
		// 读入图片资源
		cloudImgs = new BufferedImage[Constant.CLOUD_IMAGE_COUNT];
		for (int i = 0; i < Constant.CLOUD_IMAGE_COUNT; i++) { 
			cloudImgs[i] = GameUtil.loadBUfferedImage(Constant.CLOUDS_IMG_PATH[i]);
		}

		// 初始化云朵的属性
		cloudDir = Cloud.DIR_LEFT;
		time = System.currentTimeMillis(); // 获取当前时间，用于控制云的逻辑运算周期
	}