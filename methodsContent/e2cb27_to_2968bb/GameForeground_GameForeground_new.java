    public GameForeground() {
        clouds = new ArrayList<>(); //云朵的容器

        // 读入图片资源
        cloudImages = new BufferedImage[Constant.CLOUD_IMAGE_COUNT];
        for (int i = 0; i < Constant.CLOUD_IMAGE_COUNT; i++) {
            cloudImages[i] = GameUtil.loadBufferedImage(Constant.CLOUDS_IMG_PATH[i]);
        }
        time = System.currentTimeMillis(); // 获取当前时间，用于控制云的逻辑运算周期
    }