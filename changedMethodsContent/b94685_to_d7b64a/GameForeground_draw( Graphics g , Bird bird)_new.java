	public void draw(Graphics g, Bird bird) {
		cloudLogic();
		for (int i = 0; i < clouds.size(); i++) {
			clouds.get(i).draw(g, bird);
		}
	}