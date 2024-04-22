	private void pipeBornLogic(Bird bird) {
		if (bird.isDead()) {
			// �����������ˮ��
			return;
		}
		if (pipes.size() == 0) {
			// ������Ϊ�գ������һ��ˮ��
			int topHeight = GameUtil.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1); // �������ˮ�ܸ߶�

			Pipe top = PipePool.get();
			top.setAttribute(Constant.FRAME_WIDTH, -Constant.TOP_PIPE_LENGTHENING,
					topHeight + Constant.TOP_PIPE_LENGTHENING, Pipe.TYPE_TOP_NORMAL, true);

			Pipe bottom = PipePool.get();
			bottom.setAttribute(Constant.FRAME_WIDTH, topHeight + VERTICAL_INTERVAL,
					Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL, Pipe.TYPE_BOTTOM_NORMAL, true);

			pipes.add(top);
			pipes.add(bottom);
		} else {
			// �ж����һ��ˮ���Ƿ���ȫ������Ϸ����
			Pipe lastPipe = pipes.get(pipes.size() - 1); // ������������һ��ˮ��
			if (lastPipe.isInFrame()) {
				int topHeight = GameUtil.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1); // �������ˮ�ܸ߶�
				int x = lastPipe.getX() + HORIZONTAL_INTERVAL; //��ˮ�ܵ�x���� = ���һ��ˮ�ܵ�x���� + ˮ�ܵļ��

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