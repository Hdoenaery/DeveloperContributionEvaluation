	public void BirdUp() {
		if (keyIsReleased()) { // 如果按键已释放
			if (state == STATE_DEAD || state == STATE_FALL || state == STATE_UP)
				return;
			state = STATE_UP;
			speed = SPEED_UP;
			MusicUtil.playFly(); // 播放音效
			wingState = 0; // 重置翅膀状态
			keyPressed();
		}
	}