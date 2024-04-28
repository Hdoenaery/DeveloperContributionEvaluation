    private void cloudBornLogic() {
        // 100ms运算一次
        if (System.currentTimeMillis() - time > CLOUD_INTERVAL) {
            time = System.currentTimeMillis(); // 重置time
            // 如果屏幕的云朵的数量小于允许的最大数量，根据给定的概率随机添加云朵
            if (clouds.size() < Constant.MAX_CLOUD_COUNT) {
                try {
                    if (GameUtil.isInProbability(Constant.CLOUD_BORN_PERCENT, 100)) { // 根据给定的概率添加云朵
                        int index = GameUtil.getRandomNumber(0, Constant.CLOUD_IMAGE_COUNT); // 随机选取云朵图片

                        // 云朵刷新的坐标
                        int x = Constant.FRAME_WIDTH; // 从屏幕左侧开始刷新
                        // y坐标随机在上1/3屏选取
                        int y = GameUtil.getRandomNumber(Constant.TOP_BAR_HEIGHT, Constant.FRAME_HEIGHT / 3);

                        //向容器中添加云朵
                        Cloud cloud = new Cloud(cloudImages[index], x, y);
                        clouds.add(cloud);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // 添加云朵

            // 若云朵飞出屏幕则从容器中移除
            for (int i = 0; i < clouds.size(); i++) {
                // 遍历容器中的云朵
                Cloud tempCloud = clouds.get(i);
                if (tempCloud.isOutFrame()) {
                    clouds.remove(i);
                    i--;
                }
            }
        }
    }