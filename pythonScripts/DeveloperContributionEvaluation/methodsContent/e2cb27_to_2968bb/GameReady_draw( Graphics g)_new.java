	public void draw(Graphics g) {
		// ����titleͼ���x��y����
		int x = Constant.FRAME_WIDTH - titleImg.getWidth() >> 1; //x����Ϊ��������
		int y = Constant.FRAME_HEIGHT / 3;  //y����Ϊ��Ϸ���ڵ�1/3��
		g.drawImage(titleImg, x, y, null); // ����

		// ʹnotice��ͼ����˸
		final int COUNT = 30; // ��˸����
		if (flash++ > COUNT)
			drawTitle(noticeImg, g);
		if (flash == COUNT * 2) // ������˸����
				flash = 0;
	}