	public void score(Bird bird) {
		if (!bird.isDead()) {
			MusicUtil.playScore();
			score += 1;
		}
	}