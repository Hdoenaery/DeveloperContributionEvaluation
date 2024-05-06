	private void cloudLogic() {
		// 100ms����һ��
		if (System.currentTimeMillis() - time > CLOUD_INTERVAL) {
			time = System.currentTimeMillis(); // ����time
			// �����Ļ���ƶ������С�������������������ݸ����ĸ����������ƶ�
			if (clouds.size() < Constant.MAX_CLOUD_COUNT) {
				try {
					if (GameUtil.isInProbability(Constant.CLOUD_BORN_PERCENT, 100)) { // ���ݸ����ĸ�������ƶ�
						int index = GameUtil.getRandomNumber(0, Constant.CLOUD_IMAGE_COUNT); // ���ѡȡ�ƶ�ͼƬ
						// �ƶ�ˢ�µ�����
						int x = Constant.FRAME_WIDTH; // ����Ļ��࿪ʼˢ��
						// ���ݵ�ǰ�ķ�������ƶ�ˢ�µ�x���꣨��ʱ���ã�
//						if (cloudDir == Cloud.DIR_LEFT) {
//						} else {
//							x = -1 * cloudImgs[index].getWidth();
//						}
						
						// y�����������1/3��ѡȡ
						int y = GameUtil.getRandomNumber(Constant.TOP_BAR_HEIGHT, Constant.FRAME_HEIGHT / 3);
						//������������ƶ�
						Cloud cloud = new Cloud(cloudImgs[index], cloudDir, x, y);
						clouds.add(cloud);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} // ����ƶ�

			// ���ƶ�ɳ���Ļ����������Ƴ�
			for (int i = 0; i < clouds.size(); i++) {
				// ���������е��ƶ�
				Cloud tempCloud = clouds.get(i);
				if (tempCloud.isOutFrame()) {
					clouds.remove(i);
					i--;
				}
			}

			/*
			 * ������ʵ�֣�����̫������ʵ����ʱע�͵� // �ƶ�����ı䷽�� try { if (GameUtil.isInProbability(1,
			 * Constant.CLOUD_DIRCHANGE)) { // �µ��Ʋʷ��� int newDir =
			 * GameUtil.getRandomNumber(Cloud.DIR_LEFT, Cloud.DIR_RIGHT + 1); if (newDir !=
			 * cloudDir) { // ����ı䣬�����Ʋ�״̬ cloudDir = newDir; for (int i = 0; i <
			 * clouds.size(); i++) { // ���������е��ƶ� Cloud tempCloud = clouds.get(i);
			 * tempCloud.setDir(newDir); } } System.out.println(cloudDir); } } catch
			 * (Exception e) { e.printStackTrace(); }
			 */
		}
	}
}