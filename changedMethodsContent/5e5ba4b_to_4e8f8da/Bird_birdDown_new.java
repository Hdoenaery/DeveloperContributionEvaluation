	public void birdDown() {
		if (state == STATE_DEAD || state == STATE_FALL)
			return; // С��������׹��ʱ����
		state = STATE_DOWN;
	}