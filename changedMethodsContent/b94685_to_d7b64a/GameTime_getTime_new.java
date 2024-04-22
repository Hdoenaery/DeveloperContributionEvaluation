	public long getTime() {
		if (timeState == STATE_READY) {
			return startTime;
		} else if (timeState == STATE_START) {
			return (System.currentTimeMillis() - startTime);
		} else {
			return (endTime - startTime);
		}
	}