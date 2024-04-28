	private void initGame() {
		background = new GameBackground();
		gameElement = new GameElementLayer();
		foreground = new GameForeground();
		ready = new GameReady();
		bird = new Bird();
		MusicUtil.load();
		setGameState(STATE_READY);

		// ��������ˢ�´��ڵ��߳�
		new Thread(this).start();
	}