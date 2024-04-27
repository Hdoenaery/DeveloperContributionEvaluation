	public void BirdDown() {
		if (state == STATE_DEAD || state == STATE_FALL)
			return;
		state = STATE_DOWN;
	}