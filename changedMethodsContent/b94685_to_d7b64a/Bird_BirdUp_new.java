	public void BirdUp() {
		if (keyIsReleased()) { // ����������ͷ�
			if (state == STATE_DEAD || state == STATE_FALL || state == STATE_UP)
				return;
			state = STATE_UP;
			speed = SPEED_UP;
			MusicUtil.playFly(); // ������Ч
			wingState = 0; // ���ó��״̬
			keyPressed();
		}
	}