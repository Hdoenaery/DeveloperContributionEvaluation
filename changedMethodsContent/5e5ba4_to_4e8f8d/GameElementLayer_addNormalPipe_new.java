	private void addNormalPipe(Pipe lastPipe) {
		int topHeight = GameUtil.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1); // 随机生成水管高度
		int x = lastPipe.getX() + HORIZONTAL_INTERVAL; // 新水管的x坐标 = 最后一对水管的x坐标 + 水管的间隔

		Pipe top = PipePool.get("Pipe");  //从水管对象池中获取对象
		
		//设置x, y, height, type属性
		top.setAttribute(x, -Constant.TOP_PIPE_LENGTHENING, topHeight + Constant.TOP_PIPE_LENGTHENING,
				Pipe.TYPE_TOP_NORMAL, true);

		Pipe bottom = PipePool.get("Pipe");
		bottom.setAttribute(x, topHeight + VERTICAL_INTERVAL, Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL,
				Pipe.TYPE_BOTTOM_NORMAL, true);

		pipes.add(top);
		pipes.add(bottom);
	}