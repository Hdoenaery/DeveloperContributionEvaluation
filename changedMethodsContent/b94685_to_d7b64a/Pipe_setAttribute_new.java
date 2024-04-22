	public void setAttribute(int x, int y, int height, int type, boolean visible) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.type = type;
		this.visible = visible;
		setRectangle(this.x, this.y, this.height);
	}