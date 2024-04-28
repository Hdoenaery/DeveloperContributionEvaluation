	private void pipeBornLogic(Bird bird) {
		if (bird.isDead()) {
			// 鸟死后不再添加水管
			return;
		}
		if (pipes.size() == 0) {
			// 若容器为空，则添加一对水管
			int topHeight = GameUtil.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1); // 随机生成水管高度

			Pipe top = PipePool.get("Pipe");
			top.setAttribute(Constant.FRAME_WIDTH, -Constant.TOP_PIPE_LENGTHENING,
					topHeight + Constant.TOP_PIPE_LENGTHENING, Pipe.TYPE_TOP_NORMAL, true);

			Pipe bottom = PipePool.get("Pipe");
			bottom.setAttribute(Constant.FRAME_WIDTH, topHeight + VERTICAL_INTERVAL,
					Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL, Pipe.TYPE_BOTTOM_NORMAL, true);

			pipes.add(top);
			pipes.add(bottom);
		} else {
			// 判断最后一对水管是否完全进入游戏窗口，若进入则添加水管
			Pipe lastPipe = pipes.get(pipes.size() - 1); // 获得容器中最后一个水管
			if (lastPipe.isInFrame()) { // 根据游戏分数难度递增
				if (GameTime.getInstance().TimeToScore() < Constant.HOVER_MOVING_SCORE) {
					try {
						if (GameUtil.isInProbability(2, 5)) {  // 40%的概率生成悬浮的普通水管
							addHoverPipe(lastPipe);
						} else {
							addNormalPipe(lastPipe);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						if (GameUtil.isInProbability(1, 4)) {  // 1/4的概率生成普通水管
							if(GameUtil.isInProbability(1, 2))  // 生成普通水管和悬浮水管的概率
								addNormalPipe(lastPipe);
							else
								addHoverPipe(lastPipe);
						} else {
							if(GameUtil.isInProbability(1, 3))  // 生成移动水管和移动悬浮水管的概率
								addMovingHoverPipe(lastPipe);
							else
							    addMovingNormalPipe(lastPipe);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}
	}