	private void cloudLogic() {
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
						// 根据当前的方向调整云朵刷新的x坐标（暂时无用）
//						if (cloudDir == Cloud.DIR_LEFT) {
//						} else {
//							x = -1 * cloudImgs[index].getWidth();
//						}
						
						// y坐标随机在上1/3屏选取
						int y = GameUtil.getRandomNumber(Constant.TOP_BAR_HEIGHT, Constant.FRAME_HEIGHT / 3);
						//向容器中添加云朵
						Cloud cloud = new Cloud(cloudImgs[index], cloudDir, x, y);
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

			/*
			 * 功能已实现，但不太符合现实，暂时注释掉 // 云朵随机改变方向， try { if (GameUtil.isInProbability(1,
			 * Constant.CLOUD_DIRCHANGE)) { // 新的云彩方向 int newDir =
			 * GameUtil.getRandomNumber(Cloud.DIR_LEFT, Cloud.DIR_RIGHT + 1); if (newDir !=
			 * cloudDir) { // 风向改变，调整云彩状态 cloudDir = newDir; for (int i = 0; i <
			 * clouds.size(); i++) { // 遍历容器中的云朵 Cloud tempCloud = clouds.get(i);
			 * tempCloud.setDir(newDir); } } System.out.println(cloudDir); } } catch
			 * (Exception e) { e.printStackTrace(); }
			 */
		}
	}
}