	private void addNormalPipe(Pipe lastPipe) {
		int topHeight = GameUtil.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1); // �������ˮ�ܸ߶�
		int x = lastPipe.getX() + HORIZONTAL_INTERVAL; // ��ˮ�ܵ�x���� = ���һ��ˮ�ܵ�x���� + ˮ�ܵļ��

		Pipe top = PipePool.get("Pipe");  //��ˮ�ܶ�����л�ȡ����
		
		//����x, y, height, type����
		top.setAttribute(x, -Constant.TOP_PIPE_LENGTHENING, topHeight + Constant.TOP_PIPE_LENGTHENING,
				Pipe.TYPE_TOP_NORMAL, true);

		Pipe bottom = PipePool.get("Pipe");
		bottom.setAttribute(x, topHeight + VERTICAL_INTERVAL, Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL,
				Pipe.TYPE_BOTTOM_NORMAL, true);

		pipes.add(top);
		pipes.add(bottom);
	}