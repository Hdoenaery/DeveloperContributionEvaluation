	public void birdDead() {
		state = STATE_DEAD;
		// ������Ϸ��������Դ
		if (overImg == null) {
			overImg = GameUtil.loadBUfferedImage(Constant.OVER_IMG_PATH);
			scoreImg = GameUtil.loadBUfferedImage(Constant.SCORE_IMG_PATH);
			againImg = GameUtil.loadBUfferedImage(Constant.AGAIN_IMG_PATH);
		}
	}