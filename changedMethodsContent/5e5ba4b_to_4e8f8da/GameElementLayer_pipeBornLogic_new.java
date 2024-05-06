	private void pipeBornLogic(Bird bird) {
		if (bird.isDead()) {
			// �����������ˮ��
			return;
		}
		if (pipes.size() == 0) {
			// ������Ϊ�գ������һ��ˮ��
			int topHeight = GameUtil.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1); // �������ˮ�ܸ߶�

			Pipe top = PipePool.get("Pipe");
			top.setAttribute(Constant.FRAME_WIDTH, -Constant.TOP_PIPE_LENGTHENING,
					topHeight + Constant.TOP_PIPE_LENGTHENING, Pipe.TYPE_TOP_NORMAL, true);

			Pipe bottom = PipePool.get("Pipe");
			bottom.setAttribute(Constant.FRAME_WIDTH, topHeight + VERTICAL_INTERVAL,
					Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL, Pipe.TYPE_BOTTOM_NORMAL, true);

			pipes.add(top);
			pipes.add(bottom);
		} else {
			// �ж����һ��ˮ���Ƿ���ȫ������Ϸ���ڣ������������ˮ��
			Pipe lastPipe = pipes.get(pipes.size() - 1); // ������������һ��ˮ��
			if (lastPipe.isInFrame()) { // ������Ϸ�����Ѷȵ���
				if (GameTime.getInstance().TimeToScore() < Constant.HOVER_MOVING_SCORE) {
					try {
						if (GameUtil.isInProbability(2, 5)) {  // 40%�ĸ���������������ͨˮ��
							addHoverPipe(lastPipe);
						} else {
							addNormalPipe(lastPipe);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						if (GameUtil.isInProbability(1, 4)) {  // 1/4�ĸ���������ͨˮ��
							if(GameUtil.isInProbability(1, 2))  // ������ͨˮ�ܺ�����ˮ�ܵĸ���
								addNormalPipe(lastPipe);
							else
								addHoverPipe(lastPipe);
						} else {
							if(GameUtil.isInProbability(1, 3))  // �����ƶ�ˮ�ܺ��ƶ�����ˮ�ܵĸ���
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