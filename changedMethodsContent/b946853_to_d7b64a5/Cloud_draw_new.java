	public void draw(Graphics g, Bird bird) {
		int speed = this.speed;
		
		if(dir == DIR_NONE)   //�Ʋʲ���
			speed = 0;
		
		x = (dir == DIR_LEFT) ? x - speed : x + speed; // �����߼�
		g.drawImage(img, x, y, scaleImageWidth, scaleImageHeight, null);
	}