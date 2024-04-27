	public void draw(Graphics g, Bird bird) {
		int speed = this.speed;
		
		if(dir == DIR_NONE)   //云彩不动
			speed = 0;
		
		x = (dir == DIR_LEFT) ? x - speed : x + speed; // 方向逻辑
		g.drawImage(img, x, y, scaleImageWidth, scaleImageHeight, null);
	}