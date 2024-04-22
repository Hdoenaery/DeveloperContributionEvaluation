	private void Fly() {
		// ���״̬��ʵ��С��������
		wingState++;
		image = birdImgs[state > STATE_FALL ? STATE_FALL : state][wingState / 10 % IMG_COUNT];

		switch (state) {
		case STATE_NORMAL:
			break;

		case STATE_UP:
			// �����ϱ߽�

			break;

		case STATE_DOWN:
			// ����ʽ
			speed -= g * T;
			h = speed * T - g * T * T / 2;
			y = (int) (y - h);
			birdRect.y = (int) (birdRect.y - h);
			// ���Ʊ߽磬��������
			break;

		case STATE_FALL:
			// ����������������
			speed -= g * T;
			h = speed * T - g * T * T / 2;
			y = (int) (y - h);
			birdRect.y = (int) (birdRect.y - h);

			// ����׹��ı߽�
			if (birdRect.y > Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImgs[state][0].getHeight() >> 1)) {
				y = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImgs[state][0].getHeight() >> 1);
				birdRect.y = Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (birdImgs[state][0].getHeight() >> 1);

				GameFrame.setGameState(GameFrame.STATE_OVER);
				BirdDead();
			}
			break;

		case STATE_DEAD:
			break;
		}

		// �����Ϸ��߽�
		if (birdRect.y < -1 * Constant.TOP_PIPE_LENGTHENING / 2) {
			birdRect.y = -1 * Constant.TOP_PIPE_LENGTHENING / 2;
			y = -1 * Constant.TOP_PIPE_LENGTHENING / 2;
		}

		if (birdRect.y > Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (image.getHeight() >> 1)) {
			BirdFall();
		}
	}