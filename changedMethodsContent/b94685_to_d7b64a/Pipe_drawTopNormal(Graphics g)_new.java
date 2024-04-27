	private void drawTopNormal(Graphics g) {
		// 拼接的个数
		int count = (height - PIPE_HEAD_HEIGHT) / PIPE_HEIGHT + 1; // 取整+1
		// 绘制水管的主体
		for (int i = 0; i < count; i++) {
			g.drawImage(imgs[0], x, y + i * PIPE_HEIGHT, null);
		}
		// 绘制水管的顶部
		g.drawImage(imgs[1], x - ((PIPE_HEAD_WIDTH - width) >> 1),
				height - Constant.TOP_PIPE_LENGTHENING - PIPE_HEAD_HEIGHT, null); // 水管头部与水管主体的宽度不同，x坐标需要处理
	}