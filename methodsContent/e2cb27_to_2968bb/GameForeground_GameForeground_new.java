    public GameForeground() {
        clouds = new ArrayList<>(); //�ƶ������

        // ����ͼƬ��Դ
        cloudImages = new BufferedImage[Constant.CLOUD_IMAGE_COUNT];
        for (int i = 0; i < Constant.CLOUD_IMAGE_COUNT; i++) {
            cloudImages[i] = GameUtil.loadBufferedImage(Constant.CLOUDS_IMG_PATH[i]);
        }
        time = System.currentTimeMillis(); // ��ȡ��ǰʱ�䣬���ڿ����Ƶ��߼���������
    }