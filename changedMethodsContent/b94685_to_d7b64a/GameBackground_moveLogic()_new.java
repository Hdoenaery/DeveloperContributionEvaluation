	private void moveLogic() {
		layerX += speed;
		if (layerX > BackgroundImg.getWidth())
			layerX = 0;
	}