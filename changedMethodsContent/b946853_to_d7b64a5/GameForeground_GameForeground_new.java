	public GameForeground() {
		clouds = new ArrayList<>(); //�ƶ������
		
		// ����ͼƬ��Դ
		cloudImgs = new BufferedImage[Constant.CLOUD_IMAGE_COUNT];
		for (int i = 0; i < Constant.CLOUD_IMAGE_COUNT; i++) { 
			cloudImgs[i] = GameUtil.loadBUfferedImage(Constant.CLOUDS_IMG_PATH[i]);
		}

		// ��ʼ���ƶ������
		cloudDir = Cloud.DIR_LEFT;
		time = System.currentTimeMillis(); // ��ȡ��ǰʱ�䣬���ڿ����Ƶ��߼���������
	}