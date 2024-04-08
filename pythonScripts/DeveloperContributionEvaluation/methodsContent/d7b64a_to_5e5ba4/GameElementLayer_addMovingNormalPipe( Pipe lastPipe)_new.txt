	private void addMovingNormalPipe(Pipe lastPipe) {
		int topHeight = GameUtil.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1); // 随机生成水管高度
		int x = lastPipe.getX() + HORIZONTAL_INTERVAL; // 新水管的x坐标 = 最后一对水管的x坐标 + 水管的间隔

		Pipe top = PipePool.get("MovingPipe");
		top.setAttribute(x, -Constant.TOP_PIPE_LENGTHENING, topHeight + Constant.TOP_PIPE_LENGTHENING,
				Pipe.TYPE_TOP_HARD, true);

		Pipe bottom = PipePool.get("MovingPipe");
		bottom.setAttribute(x, topHeight + VERTICAL_INTERVAL, Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL,
				Pipe.TYPE_BOTTOM_HARD, true);

		pipes.add(top);
		pipes.add(bottom);
	}