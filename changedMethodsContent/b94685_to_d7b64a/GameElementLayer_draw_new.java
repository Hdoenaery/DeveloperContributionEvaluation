	public void draw(Graphics g, Bird bird) {
		// 遍历水管容器，如果可见则绘制，不可见则归还
		for (int i = 0; i < pipes.size(); i++) {
			Pipe pipe = pipes.get(i);
			if (pipe.isVisible()) {
				pipe.draw(g, bird);
			} else {
				Pipe remove = pipes.remove(i);
				PipePool.giveBack(remove);
				i--;
			}
		}
		// 碰撞检测
		isCollideBird(bird);
		pipeBornLogic(bird);
	}