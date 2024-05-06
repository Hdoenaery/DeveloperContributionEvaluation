	private void pipeBornLogic(Bird bird) {
		if (bird.isDead()) {
			// 鸟死后不再添加水管
			return;
		}
		if (pipes.size() == 0) {
			// 若容器为空，则添加一对水管
			int topHeight = GameUtil.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1); // 随机生成水管高度

			Pipe top = PipePool.get();
			top.setAttribute(Constant.FRAME_WIDTH, -Constant.TOP_PIPE_LENGTHENING,
					topHeight + Constant.TOP_PIPE_LENGTHENING, Pipe.TYPE_TOP_NORMAL, true);

			Pipe bottom = PipePool.get();
			bottom.setAttribute(Constant.FRAME_WIDTH, topHeight + VERTICAL_INTERVAL,
					Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL, Pipe.TYPE_BOTTOM_NORMAL, true);

			pipes.add(top);
			pipes.add(bottom);
		} else {
			// 判断最后一对水管是否完全进入游戏窗口
			Pipe lastPipe = pipes.get(pipes.size() - 1); // 获得容器中最后一个水管
			if (lastPipe.isInFrame()) {
				int topHeight = GameUtil.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1); // 随机生成水管高度
				int x = lastPipe.getX() + HORIZONTAL_INTERVAL; //新水管的x坐标 = 最后一对水管的x坐标 + 水管的间隔

				Pipe top = PipePool.get();
				top.setAttribute(x, -Constant.TOP_PIPE_LENGTHENING, topHeight + Constant.TOP_PIPE_LENGTHENING,
						Pipe.TYPE_TOP_NORMAL, true);

				Pipe bottom = PipePool.get();
				bottom.setAttribute(x, topHeight + VERTICAL_INTERVAL,
						Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL, Pipe.TYPE_BOTTOM_NORMAL, true);

				pipes.add(top);
				pipes.add(bottom);
			}
		}
	}