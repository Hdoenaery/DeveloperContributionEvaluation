	private void Fly() {
		// 翅膀状态，实现小鸟振翅飞行
		wingState++;
		image = birdImgs[state > STATE_FALL ? STATE_FALL : state][wingState / 10 % IMG_COUNT];

		switch (state) {
		case STATE_NORMAL:
			break;

		case STATE_UP:
			// 控制上边界

			break;

		case STATE_DOWN:
			// 物理公式
			speed -= g * T;
			h = speed * T - g * T * T / 2;
			y = (int) (y - h);
			birdRect.y = (int) (birdRect.y - h);
			// 控制边界，死亡条件
			break;

		case STATE_FALL:
			// 鸟死亡，自由落体
			speed -= g * T;
			h = speed * T - g * T * T / 2;
			y = (int) (y - h);
			birdRect.y = (int) (birdRect.y - h);

			// 控制坠落的边界
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

		// 控制上方边界
		if (birdRect.y < -1 * Constant.TOP_PIPE_LENGTHENING / 2) {
			birdRect.y = -1 * Constant.TOP_PIPE_LENGTHENING / 2;
			y = -1 * Constant.TOP_PIPE_LENGTHENING / 2;
		}

		if (birdRect.y > Constant.FRAME_HEIGHT - Constant.GROUND_HEIGHT - (image.getHeight() >> 1)) {
			BirdFall();
		}
	}