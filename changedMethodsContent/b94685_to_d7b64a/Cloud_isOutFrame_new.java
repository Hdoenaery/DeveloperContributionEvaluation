	public boolean isOutFrame() {
		boolean result = false;
		if (dir == DIR_LEFT) {
			if (x < -1 * scaleImageWidth) {
				return true;
			}
		} else if (dir == DIR_RIGHT) {
			if (x > Constant.FRAME_WIDTH) {
				return true;
			}
		}
		return result;
	}