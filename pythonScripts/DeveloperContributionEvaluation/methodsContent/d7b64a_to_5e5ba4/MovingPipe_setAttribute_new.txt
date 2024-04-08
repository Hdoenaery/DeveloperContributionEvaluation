	public void setAttribute(int x, int y, int height, int type, boolean visible) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.type = type;
		this.visible = visible;
		setRectangle(this.x, this.y, this.height);

		dealtY = 0;
		dir = DIR_DOWN;
		if (type == TYPE_TOP_HARD) {
			dir = DIR_UP;
		}
	}